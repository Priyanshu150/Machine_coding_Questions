import java.util.*;

class SmsNotificationSender implements NotificationSender {
    @Override
    public void send(Notification notification) { 
        // Implementation for sending SMS notification
        System.out.println("Sending SMS notification to user: " + notification.getUser().getName() + " with message: " + notification.getMessage());
    }
}