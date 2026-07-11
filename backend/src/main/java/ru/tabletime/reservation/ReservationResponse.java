package ru.tabletime.reservation;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationResponse(
        Long id,
        String bookingCode,
        String guestName,
        String phone,
        String email,
        LocalDate reservationDate,
        LocalTime reservationTime,
        Integer guestsCount,
        String seatingPreference,
        String status
) {
    static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(), reservation.getBookingCode(), reservation.getGuestName(),
                reservation.getPhone(), reservation.getEmail(), reservation.getReservationDate(),
                reservation.getReservationTime(), reservation.getGuestsCount(),
                reservation.getSeatingPreference(), reservation.getStatus()
        );
    }
}
