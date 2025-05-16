package networking.dto;

import services.IService;

import java.io.Serializable;
import java.time.LocalDateTime;

public class DestDepartureDTO implements Serializable {
    private String destination;
    private LocalDateTime departureTime;

    public DestDepartureDTO(String destination, LocalDateTime departureTime) {
        this.destination = destination;
        this.departureTime = departureTime;
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

    @Override
    public String toString() {
        return "DestDepartureDTO:" + destination + ", DepartureTime:" + departureTime;
    }
}
