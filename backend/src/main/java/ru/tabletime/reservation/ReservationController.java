package ru.tabletime.reservation;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class ReservationController {
    private final ReservationService service;

    public ReservationController(ReservationService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse create(@RequestBody ReservationRequest request) {
        return service.create(request);
    }

    @GetMapping("/availability")
    public AvailabilityResponse checkAvailability(
            @RequestParam LocalDate date,
            @RequestParam LocalTime time,
            @RequestParam Integer guests,
            @RequestParam(defaultValue = "Не имеет значения") String location
    ) {
        return service.checkAvailability(date, time, guests, location);
    }

    @GetMapping("/{bookingCode}")
    public ReservationResponse findByCode(@PathVariable String bookingCode) {
        return service.findByCode(bookingCode);
    }

    @PatchMapping("/{bookingCode}/cancel")
    public ReservationResponse cancel(@PathVariable String bookingCode) {
        return service.cancel(bookingCode);
    }
}
