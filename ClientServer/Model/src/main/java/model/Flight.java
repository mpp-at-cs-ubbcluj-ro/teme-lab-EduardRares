package model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Flight implements Entity<String>, Comparable<Flight>, Serializable {
    private String destination;
    private LocalDateTime departureTime;
    private String airport;
    private int numberOfAvailableSeats;
    private String id;

    public Flight(String destination, LocalDateTime departureTime, String airport, int numberOfSeats) {
        this.destination = destination;
        this.departureTime = departureTime;
        this.airport = airport;
        this.numberOfAvailableSeats = numberOfSeats;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
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
