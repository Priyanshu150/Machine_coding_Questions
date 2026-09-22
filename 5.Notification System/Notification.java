import java.util.*;

public class Notification {
    private final int notificationId;
    private final User user;
    private final String message;
    private final NotificationChannel channel;
    private final Priority priority;
    private NotificationStatus status;      

    public Notification(int notificationId, User user, String message, NotificationChannel channel, Priority priority) {
        this.notificationId = notificationId;
        this.user = user;
        this.message = message;
        this.channel = channel;
        this.priority = priority;
        this.status = NotificationStatus.CREATED; // Initial status
    }

    public int getNotificationId() {
        return notificationId;
    }

    public User getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public Priority getPriority() {
        return priority;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void markSent() {
        if (status != NotificationStatus.CREATED) {
            throw new IllegalStateException(
                "Only CREATED notification can be marked as SENT"
            );
        }

        status = NotificationStatus.SENT;
    }

    public void markFailed() {
        if (status != NotificationStatus.CREATED) {
            throw new IllegalStateException(
                "Only CREATED notification can be marked as FAILED"
            );
        }

        status = NotificationStatus.FAILED;
    }
}