import java.util.*;

class NotificationRepository {
    private final Map<Integer, Notification> notificationsMap;

    public NotificationRepository() {
        this.notificationsMap = new HashMap<>();
    }

    // method returns true if the notification was successfully added, false otherwise
    boolean addNotification(Notification notification) {
        if(notificationsMap.containsKey(notification.getNotificationId())) {
            return false; // Notification with the same ID already exists
        }
        notificationsMap.put(notification.getNotificationId(), notification);
        return true; 
    }

    // method returns true if the notification was successfully removed, false otherwise
    boolean removeNotification(int notificationId) {
        return notificationsMap.remove(notificationId) != null;
    }

    // method returns an Optional containing the notification if found, or an empty Optional if not found
    Optional<Notification> findNotification(int notificationId) {
        return Optional.ofNullable(notificationsMap.get(notificationId));
    }

    // method returns all notifications in the repository
    List<Notification> getAllNotifications() {
        return new ArrayList<>(notificationsMap.values());
    }
}