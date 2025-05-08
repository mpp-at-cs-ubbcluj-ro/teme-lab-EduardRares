package model;

import java.io.Serializable;
import java.time.*;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import org.hibernate.annotations.GenericGenerator;

import static java.time.ZoneOffset.UTC;

@Entity
@Table(name = "flight")
public class Flight implements model.Entity<String>, Comparable<Flight>, Serializable {
    @Column(name = "destination")
    private String destination;
    @Column(name = "departureDate")
    private long departureDateMillis;

    @Column(name = "departureTime")
    private LocalTime departureTime;
    @Column(name = "airport")
    private String airport;
    @Column(name = "numberOfAvailableSeats")
    private int numberOfAvailableSeats;
    @Id
    @Column(name = "id")
    private String id;

    public Flight(String destination, LocalDateTime departureTime, String airport, int numberOfSeats) {
        this.destination = destination;
        this.departureDateMillis = departureTime.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
        this.departureTime = departureTime.toLocalTime();
        this.airport = airport;
        this.numberOfAvailableSeats = numberOfSeats;
    }

    public Flight(String destination, long departureDateMillis, LocalTime departureTime, String airport, int numberOfSeats) {
        this.destination = destination;
        this.departureDateMillis = departureDateMillis;
        this.departureTime = departureTime;
        this.airport = airport;
        this.numberOfAvailableSeats = numberOfSeats;
    }

    public Flight() {}

    public LocalDate getDepartureDate() {
        return Instant.ofEpochMilli(departureDateMillis)
                .atZone(UTC)
                .toLocalDate();
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalTime getDepartureTimeValue() {
        return departureTime;
    }

    public LocalDateTime getDepartureTime() {
        return Instant.ofEpochMilli(departureDateMillis)
                .atZone(ZoneId.of("UTC"))
                .toLocalDate().atTime(departureTime);
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureDateMillis = departureTime.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
        this.departureTime = departureTime.toLocalTime();
    }

    public String getAirport() {
        return airport;
    }

    public void setAirport(String airport) {
        this.airport = airport;
    }

    public int getNumberOfAvailableSeats() {
        return numberOfAvailableSeats;
    }

    public void setNumberOfAvailableSeats(int numberOfAvailableSeats) {
        this.numberOfAvailableSeats = numberOfAvailableSeats;
    }

    @Override
    public int compareTo(Flight o) {
        return id.compareTo(o.id);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String s) {
        id = s;
    }

}