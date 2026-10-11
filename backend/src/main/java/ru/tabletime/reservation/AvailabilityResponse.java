package ru.tabletime.reservation;

public record AvailabilityResponse(
        boolean available,
        Integer tableNumber,
        Integer tableCapacity,
        String tableLocation,
        String message
) {
}
