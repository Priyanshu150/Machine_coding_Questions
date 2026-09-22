import java.util.*;

class EmailNotificationSender implements NotificationSender {
    @Override
    public void send(Notification notification) { 
        // Implementation for sending email notification
        System.out.println("Sending email notification to user: " + notification.getUser().getName() + " with message: " + notification.getMessage());
    }
}
