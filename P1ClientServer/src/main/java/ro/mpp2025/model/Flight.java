package ro.mpp2025.model;

import java.time.LocalDateTime;

public class Flight extends Entity<String>  {
    private String destination;
    private LocalDateTime departureTime;
    private String airport;
    private int numberOfAvailableSeats;

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
}
