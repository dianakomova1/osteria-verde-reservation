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
        LocalTime reservationEndTime,
        Integer guestsCount,
        String seatingPreference,
        String status,
        Integer tableNumber,
        Integer tableCapacity,
        String tableLocation
) {
    static ReservationResponse from(Reservation reservation) {
        var table = reservation.getRestaurantTable();
        return new ReservationResponse(
                reservation.getId(), reservation.getBookingCode(), reservation.getGuestName(),
                reservation.getPhone(), reservation.getEmail(), reservation.getReservationDate(),
                reservation.getReservationTime(), reservation.getReservationEndTime(),
                reservation.getGuestsCount(), reservation.getSeatingPreference(), reservation.getStatus(),
                table == null ? null : table.getTableNumber(),
                table == null ? null : table.getCapacity(),
                table == null ? null : table.getLocation()
        );
    }
}
