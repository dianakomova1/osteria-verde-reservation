package ru.tabletime.table;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "restaurant_tables")
public class RestaurantTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer tableNumber;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private String location;

    protected RestaurantTable() {
    }

    public RestaurantTable(Integer tableNumber, Integer capacity, String location) {
        this.tableNumber = tableNumber;
        this.capacity = capacity;
        this.location = location;
    }

    public Long getId() {
        return id;
    }

    public Integer getTableNumber() {
        return tableNumber;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public String getLocation() {
        return location;
    }
}
