package server;

import model.Flight;
import model.notification.Notification;
import model.notification.NotificationType;
import services.INotificationService;
import org.springframework.jms.core.JmsOperations;

import java.util.List;

public class NotificationServerImpl implements INotificationService {
    private JmsOperations jmsOperations;

    public NotificationServerImpl(JmsOperations jmsOperations) {
        this.jmsOperations = jmsOperations;
    }

    @Override
    public void updateFlight(Flight flight) {
        System.out.println("Updating flight: " + flight.getId());
        Notification notification = new Notification(
                NotificationType.UPDATE_Flight,
                flight.getId(),
                flight.getNumberOfAvailableSeats()
        );

        // Explicitly set the type ID property
        jmsOperations.convertAndSend(notification, message -> {
            message.setStringProperty("_notification", "model.notification.Notification"); // Full class name
            return message;
        });

        System.out.println("Sent notification: " + notification);
    }
}
