package ru.tabletime.reservation;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationRequest(
        String guestName,
        String phone,
        String email,
        LocalDate reservationDate,
        LocalTime reservationTime,
        Integer guestsCount,
        String seatingPreference
) {}
