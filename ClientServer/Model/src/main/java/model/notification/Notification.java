package model.notification;

public class Notification {
    private NotificationType type;
    private String flightNumber;
    private Integer seats;

    // 1) No-args constructor
    public Notification() {
    }

    // 2) Your existing all-args constructor
    public Notification(NotificationType type, String flightNumber, Integer seats) {
        this.type = type;
        this.flightNumber = flightNumber;
        this.seats = seats;
    }

    // 3) Getters & setters for all fields
    public NotificationType getType() {
        return type;
    }
    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getFlightNumber() {
        return flightNumber;
    }
    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public Integer getSeats() {
        return seats;
    }
    public void setSeats(Integer seats) {
        this.seats = seats;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "type=" + type +
                ", flightNumber=" + flightNumber +
                ", seats=" + seats +
                '}';
    }
}