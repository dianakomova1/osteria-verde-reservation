package ru.tabletime.reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByBookingCodeIgnoreCase(String bookingCode);

    boolean existsByBookingCode(String bookingCode);

    @Query("""
            select count(r) from Reservation r
            where r.restaurantTable.id = :tableId
              and r.reservationDate = :date
              and r.status = 'CONFIRMED'
              and r.reservationTime < :endTime
              and r.reservationEndTime > :startTime
            """)
    long countOverlapping(
            @Param("tableId") Long tableId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );
}
