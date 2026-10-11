package ru.tabletime.reservation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.tabletime.table.RestaurantTable;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ReservationRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReservationRepository repository;

    @Test
    void detectsOnlyOverlappingConfirmedReservations() {
        RestaurantTable table = entityManager.persistAndFlush(
                new RestaurantTable(1, 4, "У окна")
        );
        Reservation reservation = repository.saveAndFlush(new Reservation(
                "OV-TEST01", "Анна", "+79991234567", "anna@example.ru",
                LocalDate.of(2030, 5, 20), LocalTime.of(18, 0),
                4, "У окна", table
        ));

        assertEquals(1, repository.countOverlapping(
                table.getId(), LocalDate.of(2030, 5, 20),
                LocalTime.of(19, 0), LocalTime.of(21, 0)
        ));
        assertEquals(0, repository.countOverlapping(
                table.getId(), LocalDate.of(2030, 5, 20),
                LocalTime.of(20, 0), LocalTime.of(22, 0)
        ));

        reservation.cancel();
        repository.saveAndFlush(reservation);

        assertEquals(0, repository.countOverlapping(
                table.getId(), LocalDate.of(2030, 5, 20),
                LocalTime.of(19, 0), LocalTime.of(21, 0)
        ));
    }
}
