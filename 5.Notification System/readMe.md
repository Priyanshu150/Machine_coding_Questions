# 🔔 Notification System

> **Level 3 — Design Thinking**

An in-memory Notification System built using core Java, demonstrating the **Strategy Design Pattern**, **Separation of Concerns**, and clean use of Functional Interfaces to route and process notifications across multiple channels.

---

## 📌 Table of Contents

- [Problem Overview](#problem-overview)
- [Initial Design (v1)](#initial-design-v1)
  - [Enums](#enums)
  - [User](#user)
  - [Notification](#notification)
  - [NotificationSender](#notificationsender)
  - [NotificationRepository](#notificationrepository)
  - [NotificationProcessor](#notificationprocessor)
- [Design Flaws & Improvements](#design-flaws--improvements)
- [Improved Design (v2)](#improved-design-v2)
- [Send Notification Flow](#send-notification-flow)
- [Key Design Notes](#key-design-notes)

---

## Problem Overview

Design and implement a **Notification System** that can send notifications to users across multiple channels (Email, SMS, Push). The system should support filtering, processing, and tracking the status of each notification — all in-memory, no database or UI.

---

## Initial Design (v1)

### Enums

```java
enum NotificationChannel {
    EMAIL,
    SMS,
    PUSH
}

enum NotificationStatus {
    CREATED,
    SENT,
    FAILED
}

enum Priority {
    LOW,
    MEDIUM,
    HIGH
}
```

---

### `User`

```java
public class User {
    private final int userId;        // unique
    private final String name;
    private final String contact;       // email / phone number
}
```

---

### `Notification`

```java
public class Notification {
    private final int notificationId;
    private final User user;
    private final String message;
    private final NotificationChannel channel;
    private final Priority priority;
    private NotificationStatus status;      // mutable
}
```

---

### `NotificationSender`

```java
interface NotificationSender {
    void send(Notification notification);
}

class EmailNotificationSender implements NotificationSender {
    @Override
    public void send(Notification notification) { ... }
}

class SmsNotificationSender implements NotificationSender {
    @Override
    public void send(Notification notification) { ... }
}

class PushNotificationSender implements NotificationSender {
    @Override
    public void send(Notification notification) { ... }
}
```

---

### `NotificationRepository`

```java
class NotificationRepository {
    private final Map<Integer, Notification> notificationsMap;

    boolean addNotification(Notification notification) { ... }
    boolean removeNotification(int notificationId) { ... }
    Optional<Notification> findNotification(int notificationId) { ... }
    List<Notification> getAllNotifications() { ... }
}
```

---

### `NotificationProcessor`

```java
class NotificationProcessor {
    NotificationRepository repository;
    NotificationSender sender;          // ⚠️ see Flaw #1

    // Functional operations
    List<Notification> filter(Predicate<Notification> condition);
    <R> List<R> transform(Function<Notification, R> mapper);
    void processEach(Consumer<Notification> action);
    List<Notification> sort(Comparator<Notification> comparator);

    // Business operations
    void sendNotification(int notificationId);

    void sendPendingNotifications() {   // ⚠️ see Flaw #3
        if (notification.getStatus() == NotificationStatus.CREATED) {
            // body
        }
    }

    List<Notification> getNotificationsByPriority(Priority priority);
    long countByStatus(NotificationStatus status);
    Map<NotificationChannel, Long> countByChannel();
    Map<Priority, Long> countByPriority();
}
```

**Intended structure (v1):**
```
NotificationProcessor
       │
       ├── NotificationRepository
       │
       └── NotificationSender  ← single sender; channel not considered
```

---

## Design Flaws & Improvements

### Flaw 1 — Single `NotificationSender` is not extensible

`NotificationProcessor` holds one `NotificationSender`, meaning only one channel can be used at a time. Switching channels requires changing the class.

**Fix:** Use `Map<NotificationChannel, NotificationSender>` to hold all senders and dispatch to the correct one at runtime based on the notification's channel.

```
NotificationProcessor
    │
    ├── repository
    │
    └── senders (Map<NotificationChannel, NotificationSender>)
          │
          ├── EMAIL → EmailNotificationSender
          ├── SMS   → SmsNotificationSender
          └── PUSH  → PushNotificationSender
```

---

### Flaw 2 — `setStatus()` allows invalid state transitions

A generic `setStatus(SENT)` can be called from anywhere and at any point, making illegal transitions (e.g. re-sending a failed notification) possible.

**Fix:** Replace `setStatus()` with guarded transition methods that enforce valid state changes:

```java
public void markSent() {
    if (status != NotificationStatus.CREATED) {
        throw new IllegalStateException(
            "Only CREATED notifications can be marked SENT"
        );
    }
    status = NotificationStatus.SENT;
}

public void markFailed() {
    if (status != NotificationStatus.CREATED) {
        throw new IllegalStateException(
            "Only CREATED notifications can be marked FAILED"
        );
    }
    status = NotificationStatus.FAILED;
}
```

Valid transitions:
```
CREATED → SENT      ✅
CREATED → FAILED    ✅
SENT    → FAILED    ❌ throws IllegalStateException
FAILED  → SENT      ❌ throws IllegalStateException
```

---

### Flaw 3 — `sendPendingNotifications()` manually duplicates filter logic

The method uses a raw `if` check instead of reusing the `filter(Predicate)` method already available in the same class.

**Fix:** Delegate to `filter` + `forEach` — keeping the code DRY and showing the functional layer and business layer working together:

```java
void sendPendingNotifications() {
    List<Notification> pending =
        filter(n -> n.getStatus() == NotificationStatus.CREATED);

    pending.forEach(n -> sendNotification(n.getNotificationId()));
}
```

---

### Flaw 4 — `sendNotification()` silently does nothing if notification is not found

If a non-existent ID is passed, the method fails silently — no error, no feedback.

**Fix:** Use `.orElseThrow()` to surface a clear, immediate exception:

```java
Notification notification = repository.findNotification(notificationId)
    .orElseThrow(() -> new IllegalArgumentException(
        "Notification not found: " + notificationId
    ));
```

---

## Improved Design (v2)

### `NotificationProcessor`

```java
class NotificationProcessor {
    private final NotificationRepository repository;
    private final Map<NotificationChannel, NotificationSender> senders;

    public NotificationProcessor(
        NotificationRepository repository,
        Map<NotificationChannel, NotificationSender> senders
    ) {
        this.repository = repository;
        this.senders = senders;
    }
}
```

**Usage:**

```java
Map<NotificationChannel, NotificationSender> senders = Map.of(
    NotificationChannel.EMAIL, new EmailNotificationSender(),
    NotificationChannel.SMS,   new SmsNotificationSender(),
    NotificationChannel.PUSH,  new PushNotificationSender()
);

NotificationProcessor processor = new NotificationProcessor(repository, senders);
```

### `NotificationProcessor` — Full Method Reference

#### Functional Operations

| Method | Functional Interface | Description |
|---|---|---|
| `filter(Predicate<Notification>)` | `Predicate<T>` | Return notifications matching a condition |
| `transform(Function<Notification, R>)` | `Function<T, R>` | Map each notification to another type/value |
| `processEach(Consumer<Notification>)` | `Consumer<T>` | Perform an action on each notification |
| `sort(Comparator<Notification>)` | `Comparator<T>` | Return notifications in a custom order |

#### Business Operations

| Method | Return Type | Description |
|---|---|---|
| `sendNotification(int notificationId)` | `void` | Look up and send a single notification |
| `sendPendingNotifications()` | `void` | Send all notifications with status `CREATED` |
| `getNotificationsByPriority(Priority)` | `List<Notification>` | Filter by priority level |
| `countByStatus(NotificationStatus)` | `long` | Count notifications by status |
| `countByChannel()` | `Map<NotificationChannel, Long>` | Count notifications per channel |
| `countByPriority()` | `Map<Priority, Long>` | Count notifications per priority |

---

## Send Notification Flow

```
sendNotification(notificationId)
        │
        ▼
repository.findNotification(id)
        │
        ├── not found → throw IllegalArgumentException("Notification not found: " + id)
        │
        ▼
   Notification
        │
        ▼
  check status == CREATED
        │
        ▼
  notification.getChannel()
        │
        ▼
  senders.get(channel)        ← dispatches to correct sender via map
        │
        ▼
  sender.send(notification)
        │
        ├── success   → notification.markSent()
        │
        └── exception → notification.markFailed()
```

---

## Key Design Notes

**1. Strategy Pattern for Senders**

Each `NotificationSender` implementation is a strategy. `NotificationProcessor` selects the right one at runtime via the `senders` map. Adding a new channel (e.g. `WHATSAPP`) means adding one new class and one new map entry — nothing else changes. This is the **Open/Closed Principle** in action.

**2. Guarded State Transitions over `setStatus()`**

`markSent()` and `markFailed()` enforce the state machine at the source, making illegal transitions unrepresentable at runtime.

**3. `.orElseThrow()` over Silent Failures**

Failing silently is worse than a clear exception. `.orElseThrow()` surfaces the problem immediately with a useful message.

**4. Functional Interfaces Reused Inside Business Methods**

`sendPendingNotifications()` delegates to `filter(Predicate)` rather than duplicating the logic. This keeps the code DRY and shows how the functional layer and business layer work together.