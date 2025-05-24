package client.ams;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Employee;
import model.Flight;
import model.notification.Notification;
import services.IServiceAMS;
import services.NotificationReceiver;
import services.NotificationSubscriber;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ClientCtrlAMS implements NotificationSubscriber {
    private Employee employee;
    private final IServiceAMS server;
    private NotificationReceiver receiver;

    private final ObservableList<Flight> flightsModel = FXCollections.observableArrayList();

    private Consumer<Notification> onNotification;

    public ClientCtrlAMS(IServiceAMS server) {
        this.server = server;
    }

    public void setReceiver(NotificationReceiver receiver) {
        this.receiver = receiver;
    }

    public void setOnNotification(Consumer<Notification> onNotification) {
        this.onNotification = onNotification;
    }

    public ObservableList<Flight> getFlightsModel() {
        return flightsModel;
    }

    public Optional<Employee> login(String username, String password) {
        Optional<Employee> emp = server.login(username, password);
        emp.ifPresent(e -> {
            this.employee = e;
            List<Flight> flights = server.getAllFlights();
            flightsModel.setAll(flights);
            receiver.start(this);
        });
        return emp;
    }

    @Override
    public void noticationReceived(Notification notification) {
        Platform.runLater(() -> {
            flightsModel.stream()
                    .filter(f -> f.getId().equals(notification.getFlightNumber()))
                    .findFirst()
                    .ifPresent(f -> f.setNumberOfAvailableSeats(notification.getSeats()));
            if (onNotification != null) {
                onNotification.accept(notification);
            }
        });
    }

    public void logout() {
        try {
            server.logout(employee);
        } finally {
            receiver.stop();
        }
    }

    public void addTicket(model.Ticket ticket) {
        server.addTicket(ticket);
    }

    public void updateFlight(Flight flight) {
        server.updateFlight(flight);
    }

    public List<Flight> getFilteredFlightsModel(String destination, java.time.LocalDateTime departureTime) {
        return server.getbyDestDeparture(destination, departureTime);
    }
}