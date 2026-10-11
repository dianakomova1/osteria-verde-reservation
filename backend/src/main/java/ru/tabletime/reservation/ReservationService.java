package ru.tabletime.reservation;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.tabletime.table.RestaurantTable;
import ru.tabletime.table.RestaurantTableRepository;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class ReservationService {
    private static final String CODE_CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+7\\d{10}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    private final SecureRandom random = new SecureRandom();
    private final ReservationRepository repository;
    private final RestaurantTableRepository tableRepository;

    public ReservationService(ReservationRepository repository, RestaurantTableRepository tableRepository) {
        this.repository = repository;
        this.tableRepository = tableRepository;
    }

    @Transactional
    public ReservationResponse create(ReservationRequest request) {
        validate(request);

        List<RestaurantTable> suitableTables = tableRepository.findSuitableForUpdate(request.guestsCount());
        RestaurantTable table = findAvailableTable(
                request.reservationDate(),
                request.reservationTime(),
                request.seatingPreference(),
                suitableTables
        );
        if (table == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "На выбранное время нет свободного столика для указанного количества гостей"
            );
        }

        Reservation reservation = new Reservation(
                createBookingCode(), request.guestName(), request.phone(), request.email(),
                request.reservationDate(), request.reservationTime(), request.guestsCount(),
                request.seatingPreference(), table
        );
        return ReservationResponse.from(repository.save(reservation));
    }

    @Transactional(readOnly = true)
    public AvailabilityResponse checkAvailability(
            LocalDate date,
            LocalTime time,
            Integer guestsCount,
            String seatingPreference
    ) {
        validateVisit(date, time, guestsCount, seatingPreference);

        RestaurantTable table = findAvailableTable(
                date,
                time,
                seatingPreference,
                tableRepository.findByCapacityGreaterThanEqualOrderByCapacityAscTableNumberAsc(guestsCount)
        );
        if (table == null) {
            return new AvailabilityResponse(
                    false, null, null, null,
                    "К сожалению, на это время подходящих столиков нет. Попробуйте выбрать другое время."
            );
        }

        return new AvailabilityResponse(
                true,
                table.getTableNumber(),
                table.getCapacity(),
                table.getLocation(),
                "Свободный столик найден"
        );
    }

    @Transactional(readOnly = true)
    public ReservationResponse findByCode(String bookingCode) {
        if (bookingCode == null || bookingCode.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Укажите номер брони");
        }
        return repository.findByBookingCodeIgnoreCase(bookingCode.trim())
                .map(ReservationResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Бронь с таким номером не найдена"));
    }

    @Transactional
    public ReservationResponse cancel(String bookingCode) {
        Reservation reservation = repository.findByBookingCodeIgnoreCase(bookingCode.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Бронь с таким номером не найдена"));
        if ("CANCELLED".equals(reservation.getStatus())) {
            return ReservationResponse.from(reservation);
        }
        reservation.cancel();
        return ReservationResponse.from(repository.save(reservation));
    }

    private RestaurantTable findAvailableTable(
            LocalDate date,
            LocalTime time,
            String seatingPreference,
            List<RestaurantTable> suitableTables
    ) {
        LocalTime endTime = time.plusHours(2);

        return suitableTables.stream()
                .sorted(Comparator
                        .comparing((RestaurantTable table) -> !matchesPreference(table, seatingPreference))
                        .thenComparing(RestaurantTable::getCapacity)
                        .thenComparing(RestaurantTable::getTableNumber))
                .filter(table -> repository.countOverlapping(
                        table.getId(), date, time, endTime
                ) == 0)
                .findFirst()
                .orElse(null);
    }

    private boolean matchesPreference(RestaurantTable table, String preference) {
        return preference == null
                || preference.isBlank()
                || "Не имеет значения".equalsIgnoreCase(preference)
                || table.getLocation().equalsIgnoreCase(preference);
    }

    private void validate(ReservationRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Данные бронирования не переданы");
        }
        if (request.guestName() == null || request.guestName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Укажите имя гостя");
        }
        if (request.phone() == null || !PHONE_PATTERN.matcher(request.phone()).matches()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Телефон должен быть указан в формате +79991234567"
            );
        }
        if (request.email() == null || !EMAIL_PATTERN.matcher(request.email()).matches()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Email должен быть указан в формате name@example.ru"
            );
        }
        validateVisit(
                request.reservationDate(),
                request.reservationTime(),
                request.guestsCount(),
                request.seatingPreference()
        );
    }

    private void validateVisit(
            LocalDate reservationDate,
            LocalTime reservationTime,
            Integer guestsCount,
            String seatingPreference
    ) {
        if (reservationDate == null || reservationDate.isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Дата бронирования не может быть в прошлом");
        }
        if (reservationTime == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Укажите время бронирования");
        }
        if (LocalDateTime.of(reservationDate, reservationTime).isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Время бронирования уже прошло");
        }
        if (reservationTime.isBefore(LocalTime.of(12, 0))
                || reservationTime.isAfter(LocalTime.of(21, 0))
                || reservationTime.getMinute() % 30 != 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Выберите время с 12:00 до 21:00 с интервалом 30 минут"
            );
        }
        if (guestsCount == null || guestsCount < 1 || guestsCount > 12) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Количество гостей должно быть от 1 до 12");
        }
        if (seatingPreference == null || seatingPreference.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Укажите предпочтение по расположению");
        }
    }

    private String createBookingCode() {
        String code;
        do {
            StringBuilder value = new StringBuilder("OV-");
            for (int i = 0; i < 6; i++) {
                value.append(CODE_CHARACTERS.charAt(random.nextInt(CODE_CHARACTERS.length())));
            }
            code = value.toString();
        } while (repository.existsByBookingCode(code));
        return code;
    }
}
