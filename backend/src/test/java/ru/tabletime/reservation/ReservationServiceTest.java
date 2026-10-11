package ru.tabletime.reservation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import ru.tabletime.table.RestaurantTable;
import ru.tabletime.table.RestaurantTableRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {
    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private RestaurantTableRepository tableRepository;

    @Mock
    private RestaurantTable table;

    private ReservationService service;

    @BeforeEach
    void setUp() {
        service = new ReservationService(reservationRepository, tableRepository);
    }

    @Test
    void createsReservationWhenTableIsAvailable() {
        when(table.getId()).thenReturn(1L);
        when(table.getTableNumber()).thenReturn(4);
        when(table.getCapacity()).thenReturn(4);
        when(table.getLocation()).thenReturn("Основной зал");
        when(tableRepository.findSuitableForUpdate(4)).thenReturn(List.of(table));
        when(reservationRepository.countOverlapping(any(), any(), any(), any())).thenReturn(0L);
        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ReservationResponse response = service.create(validRequest(4));

        assertEquals("CONFIRMED", response.status());
        assertEquals(4, response.tableNumber());
        assertEquals("Основной зал", response.tableLocation());
    }

    @Test
    void rejectsDateInThePast() {
        ReservationRequest request = new ReservationRequest(
                "Анна", "+79991234567", "anna@example.ru",
                LocalDate.now().minusDays(1), LocalTime.of(18, 0), 2, "Не имеет значения"
        );

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> service.create(request)
        );

        assertEquals(400, error.getStatusCode().value());
    }

    @Test
    void rejectsInvalidGuestData() {
        ReservationRequest request = new ReservationRequest(
                "", "89991234567", "wrong-email",
                LocalDate.now().plusDays(1), LocalTime.of(18, 0), 0, "Не имеет значения"
        );

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> service.create(request)
        );

        assertEquals(400, error.getStatusCode().value());
    }

    @Test
    void rejectsBookingWhenEverySuitableTableIsOccupied() {
        when(table.getId()).thenReturn(1L);
        when(tableRepository.findSuitableForUpdate(2)).thenReturn(List.of(table));
        when(reservationRepository.countOverlapping(any(), any(), any(), any())).thenReturn(1L);

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> service.create(validRequest(2))
        );

        assertEquals(409, error.getStatusCode().value());
    }

    private ReservationRequest validRequest(int guestsCount) {
        return new ReservationRequest(
                "Анна", "+79991234567", "anna@example.ru",
                LocalDate.now().plusDays(1), LocalTime.of(18, 0),
                guestsCount, "Не имеет значения"
        );
    }
}
