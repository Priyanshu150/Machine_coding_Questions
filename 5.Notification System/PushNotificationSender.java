import java.util.*;


class PushNotificationSender implements NotificationSender {
    @Override
    public void send(Notification notification) { 
        // Implementation for sending push notification
        System.out.println("Sending push notification to user: " + notification.getUser().getName() + " with message: " + notification.getMessage());
    }
}