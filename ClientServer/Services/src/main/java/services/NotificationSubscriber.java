package services;

import model.notification.Notification;

public interface NotificationSubscriber {
    void noticationReceived(Notification notification);
}
