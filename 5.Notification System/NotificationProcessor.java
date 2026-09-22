import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class NotificationProcessor {
    private final NotificationRepository repository;
    private final Map<NotificationChannel, NotificationSender> senders;

    public NotificationProcessor(NotificationRepository repository, Map<NotificationChannel, NotificationSender> senders) {
        this.repository = repository;
        this.senders = senders;
    }          

    // Functional operations
    public List<Notification> filter(Predicate<Notification> condition){
        return repository.getAllNotifications().stream()
                .filter(condition)
                .toList();
    }

    public <R> List<R> transform(Function<Notification, R> mapper) {
        return repository.getAllNotifications().stream()
                .map(mapper)
                .toList();
    }

    public void processEach(Consumer<Notification> action) {
        repository.getAllNotifications().forEach(action);
    }

    public List<Notification> sort(Comparator<Notification> comparator) {
        return repository.getAllNotifications().stream()
                .sorted(comparator)
                .toList();
    }

    // Business operations
    public void sendNotification(int notificationId){
        Notification notification = repository.findNotification(notificationId)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "Notification with ID "
                    + notificationId
                    + " not found."
                )
            );

        if(notification.getStatus() != NotificationStatus.CREATED) {
            throw new IllegalStateException("Notification with ID " + notificationId + " is not in CREATED status.");
        }

        NotificationSender sender = senders.get(notification.getChannel());
        if(sender == null) {
            throw new IllegalArgumentException("No sender found for channel: " + notification.getChannel());
        }

        try {
            sender.send(notification);
            notification.markSent();

        } catch (Exception e) {
            notification.markFailed();
            throw e;
        }
    }

    void sendPendingNotifications() {
        List<Notification> pending = filter(notification -> notification.getStatus() == NotificationStatus.CREATED);
        pending.forEach(n -> sendNotification(n.getNotificationId()));
    }

    List<Notification> getNotificationsByPriority(Priority priority){
        return filter(notification -> notification.getPriority() == priority);
    }

    long countByStatus(NotificationStatus status){
        return filter(notification -> notification.getStatus() == status).size();
    }
    Map<NotificationChannel, Long> countByChannel(){
        return repository.getAllNotifications().stream()
                .collect(Collectors.groupingBy(Notification::getChannel, Collectors.counting()));
    }

    Map<Priority, Long> countByPriority(){
        return repository.getAllNotifications().stream()
                .collect(Collectors.groupingBy(Notification::getPriority, Collectors.counting()));
    }
}