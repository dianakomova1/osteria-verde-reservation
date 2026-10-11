package ru.tabletime.reservation;

import jakarta.persistence.*;
import ru.tabletime.table.RestaurantTable;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String bookingCode;

    @Column(nullable = false)
    private String guestName;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private LocalDate reservationDate;

    @Column(nullable = false)
    private LocalTime reservationTime;

    @Column
    private LocalTime reservationEndTime;

    @Column(nullable = false)
    private Integer guestsCount;

    @Column
    private String seatingPreference;

    @Column(nullable = false)
    private String status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "restaurant_table_id")
    private RestaurantTable restaurantTable;

    protected Reservation() {}

    public Reservation(String bookingCode, String guestName, String phone, String email,
                       LocalDate reservationDate, LocalTime reservationTime, Integer guestsCount,
                       String seatingPreference, RestaurantTable restaurantTable) {
        this.bookingCode = bookingCode;
        this.guestName = guestName;
        this.phone = phone;
        this.email = email;
        this.reservationDate = reservationDate;
        this.reservationTime = reservationTime;
        this.reservationEndTime = reservationTime.plusHours(2);
        this.guestsCount = guestsCount;
        this.seatingPreference = seatingPreference;
        this.status = "CONFIRMED";
        this.restaurantTable = restaurantTable;
    }

    public Long getId() { return id; }
    public String getBookingCode() { return bookingCode; }
    public String getGuestName() { return guestName; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public LocalDate getReservationDate() { return reservationDate; }
    public LocalTime getReservationTime() { return reservationTime; }
    public LocalTime getReservationEndTime() { return reservationEndTime; }
    public Integer getGuestsCount() { return guestsCount; }
    public String getSeatingPreference() { return seatingPreference; }
    public String getStatus() { return status; }
    public RestaurantTable getRestaurantTable() { return restaurantTable; }

    public void cancel() {
        this.status = "CANCELLED";
    }
}
