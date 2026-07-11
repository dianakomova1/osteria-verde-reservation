package ru.tabletime.reservation;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.security.SecureRandom;
import java.time.LocalDate;
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

    public ReservationService(ReservationRepository repository) {
        this.repository = repository;
    }

    public ReservationResponse create(ReservationRequest request) {
        validate(request);

        Reservation reservation = new Reservation(
                createBookingCode(), request.guestName(), request.phone(), request.email(),
                request.reservationDate(), request.reservationTime(), request.guestsCount(),
                request.seatingPreference()
        );
        return ReservationResponse.from(repository.save(reservation));
    }

    private void validate(ReservationRequest request) {
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
        if (request.reservationDate() == null || request.reservationDate().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Дата бронирования не может быть в прошлом");
        }
        if (request.reservationTime() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Укажите время бронирования");
        }
        if (request.guestsCount() == null || request.guestsCount() < 1 || request.guestsCount() > 12) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Количество гостей должно быть от 1 до 12");
        }
        if (request.seatingPreference() == null || request.seatingPreference().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Укажите предпочтение по расположению");
        }
    }

    private String createBookingCode() {
        StringBuilder code = new StringBuilder("TT-");
        for (int i = 0; i < 6; i++) {
            code.append(CODE_CHARACTERS.charAt(random.nextInt(CODE_CHARACTERS.length())));
        }
        return code.toString();
    }
}
