package client.ams;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import model.notification.Notification;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.support.converter.MessageConverter;
import services.NotificationReceiver;
import services.NotificationSubscriber;

public class NotificationReceiverImpl implements NotificationReceiver {
    private final DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
    private final MessageConverter converter;

    public NotificationReceiverImpl(ConnectionFactory cf, MessageConverter converter) {
        this.converter = converter;
        container.setConnectionFactory(cf);
        container.setPubSubDomain(true);
        container.setMessageConverter(converter);
        container.setDestinationName("FlightUpdatesTopic");
        container.setSubscriptionDurable(false);
    }

    @Override
    public void start(NotificationSubscriber subscriber) {
        container.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    Notification notification = (Notification) converter.fromMessage(message);
                    System.out.println("Received notification: " + notification);
                    subscriber.noticationReceived(notification);
                } catch (Exception e) {
                    System.err.println("Error converting or delivering JMS message:");
                    e.printStackTrace();
                }
            }
        });

        container.afterPropertiesSet();
        container.start();
        System.out.println("Subscribed to topic FlightUpdatesTopic");
    }

    @Override
    public void stop() {
        container.stop();
        container.destroy();
    }
}