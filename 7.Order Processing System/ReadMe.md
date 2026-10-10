# 💳 Payment Gateway

> **Level 3 — Design Thinking**

An in-memory Payment Gateway system built using core Java, demonstrating **Strategy Pattern** for payment methods, **guarded state transitions** for order and payment lifecycle, and **separation of concerns** across repository, service, and domain layers.

---

## 📌 Table of Contents

- [Problem Overview](#problem-overview)
- [Class Design](#class-design)
  - [Enums](#enums)
  - [Customer](#customer)
  - [Product](#product)
  - [OrderItem](#orderitem)
  - [Order](#order)
  - [Payment](#payment)
  - [PaymentGateway](#paymentgateway)
  - [OrderRepository](#orderrepository)
  - [PaymentRepository](#paymentrepository)
  - [OrderService](#orderservice)
- [Design Review & Improvements](#design-review--improvements)
- [Backend Thinking — API Design](#backend-thinking--api-design)

---

## Problem Overview

Design and implement a **Payment Gateway** system that allows customers to create orders, add products, choose a payment method, and process payments — with full lifecycle tracking for both orders and payments.

---

## Class Design

### Enums

```java
enum OrderStatus {
    CREATED,
    PAYMENT_PENDING,
    PAID,
    CONFIRMED,
    CANCELLED,
    PAYMENT_FAILED
}

enum PaymentMethod {
    CREDIT_CARD,
    DEBIT_CARD,
    UPI,
    PAYPAL
}

enum PaymentStatus {
    INITIATED,
    SUCCESS,
    FAILED
}
```

---

### `Customer`

```java
class Customer {
    private final int customerId;
    private final String name;
    private final String email;

    @Override
    public int hashCode() { ... }

    @Override
    public boolean equals(Object o) { ... }
}
```

> `hashCode()` and `equals()` are overridden so `Customer` can be used correctly as a key in `Map` or as an element in `Set`.

---

### `Product`

```java
class Product {
    private final int productId;
    private final String name;
    private final BigDecimal price;

    @Override
    public int hashCode() {
        return Integer.hashCode(productId);
    }

    @Override
    public boolean equals(Object o) { ... }
}
```

> Use `BigDecimal` for price — never `double` or `float` for financial values due to floating-point precision issues.

---

### `OrderItem`

A snapshot of a product at the time it was added to the order. `unitPrice` is captured at add-time so price changes to `Product` don't affect existing orders.

```java
class OrderItem {
    private final Product product;
    private int quantity;               // not final — quantity can be updated if same product added again
    private final BigDecimal unitPrice; // snapshot of price at time of adding
}
```

---

### `Order`

Tracks the full lifecycle of a customer's order. Status transitions are guarded — only valid transitions are allowed.

Initial state: `status = CREATED`

```java
class Order {
    private final int orderId;
    private final Customer customer;
    private final List<OrderItem> items;
    private OrderStatus status;         // mutable — guarded transitions
    // PaymentMethod removed — passed at processPayment() instead (see Design Review #2)
}
```

**`addItem()` rules:**
- Quantity must be greater than 0
- Order cannot accept items after it has been `PAID`
- If the same product is added again, increase the existing quantity

```java
public boolean addItem(Product product, int quantity) { ... }

public BigDecimal calculateTotal() { ... }
```

**State Transition Methods:**

```java
public boolean startPayment()       // CREATED        → PAYMENT_PENDING
public boolean paymentSuccessful()  // PAYMENT_PENDING → PAID
public boolean paymentFailed()      // PAYMENT_PENDING → PAYMENT_FAILED
public boolean confirm()            // PAID            → CONFIRMED
public boolean cancel()             // CREATED / PAYMENT_FAILED → CANCELLED
```

---

### `Payment`

Represents a single payment attempt for an order. Initial status is `INITIATED`.

```java
class Payment {
    private final int paymentId;
    private final int orderId;
    private final BigDecimal amount;
    private final PaymentMethod paymentMethod;
    private PaymentStatus status;       // mutable — INITIATED → SUCCESS / FAILED

    public void markSuccess() { ... }
    public void markFailed()  { ... }
}
```

> Use `AtomicInteger` for thread-safe ID generation (see Design Review #3).

---

### `PaymentGateway`

Each payment method has its own gateway implementation. Adding a new payment method requires only a new class — no existing code changes.

```java
interface PaymentGateway {
    Payment processPayment(Order order, PaymentMethod paymentMethod);
}

class CreditCardPaymentGateway implements PaymentGateway {
    @Override
    public Payment processPayment(Order order, PaymentMethod paymentMethod) { ... }
}

class DebitCardPaymentGateway implements PaymentGateway {
    @Override
    public Payment processPayment(Order order, PaymentMethod paymentMethod) { ... }
}

class UPIPaymentGateway implements PaymentGateway {
    @Override
    public Payment processPayment(Order order, PaymentMethod paymentMethod) { ... }
}

class PaypalPaymentGateway implements PaymentGateway {
    @Override
    public Payment processPayment(Order order, PaymentMethod paymentMethod) { ... }
}
```

---

### `OrderRepository`

```java
class OrderRepository {
    private final Map<Integer, Order> orderMap;     // orderId → Order

    public boolean save(Order order)                { ... }
    public Optional<Order> findById(int orderId)    { ... }
    public boolean delete(int orderId)              { ... }
    public List<Order> findAll()                    { ... }
}
```

---

### `PaymentRepository`

```java
class PaymentRepository {
    private final Map<Integer, Payment> paymentMap;  // paymentId → Payment

    public boolean save(Payment payment)                        { ... }
    public Optional<Payment> findById(int paymentId)            { ... }
    public Optional<Payment> findByOrderId(int orderId)         { ... }
    public List<Payment> findAll()                              { ... }
}
```

---

### `OrderService`

The central orchestrator. Holds repositories and a **map of all payment gateways** keyed by `PaymentMethod`.

```java
class OrderService {
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final Map<PaymentMethod, PaymentGateway> gateways;
}
```

**Order Management:**

| Method | Return Type | Description |
|---|---|---|
| `createOrder(Customer customer)` | `Order` | Create a new order for a customer |
| `addProduct(int orderId, Product product, int quantity)` | `void` | Add or update a product in the order |
| `getOrder(int orderId)` | `Optional<Order>` | Find an order by ID |
| `calculateTotal(int orderId)` | `BigDecimal` | Compute the total for all items |
| `processPayment(int orderId, PaymentMethod method)` | `Payment` | Initiate payment via the selected gateway |
| `confirmOrder(int orderId)` | `void` | Confirm a successfully paid order |
| `cancelOrder(int orderId)` | `void` | Cancel an order |
| `retryPayment(int orderId, PaymentMethod method)` | `Payment` | Retry a failed payment |

**Query Operations:**

| Method | Return Type | Description |
|---|---|---|
| `findByCustomer(int customerId)` | `List<Order>` | All orders for a customer |
| `findByStatus(OrderStatus status)` | `List<Order>` | Orders filtered by status |
| `findOrdersAboveAmount(BigDecimal amount)` | `List<Order>` | Orders with total above a threshold |

**Statistics:**

| Method | Return Type | Description |
|---|---|---|
| `countOrdersByStatus()` | `Map<OrderStatus, Long>` | Count of orders grouped by status |
| `totalConfirmedOrderValue()` | `BigDecimal` | Sum of all `CONFIRMED` order totals |
| `highestValueOrder()` | `Optional<Order>` | The single highest-value order |

---

## Design Review & Improvements

### Issue 1 — `OrderItem.quantity` cannot be `final`

If the same product is added again, the requirement says to increase the existing quantity. A `final` field cannot be updated after construction.

```java
// ❌ Before
private final int quantity;

// ✅ After
private int quantity;   // mutable — updated when same product is added again
```

---

### Issue 2 — `PaymentMethod` should not live on `Order`

Putting `PaymentMethod` as a `final` field on `Order` forces the customer to choose a payment method at order creation time — before they've decided how to pay.

**Fix:** Remove `PaymentMethod` from `Order` and pass it at payment time instead:

```java
// ❌ Before
Order createOrder(Customer customer)        // PaymentMethod set in constructor

// ✅ After
Payment processPayment(int orderId, PaymentMethod paymentMethod)
```

This way the customer can browse, add items, and only choose a payment method when they're ready to pay:

```
Order created
    ↓
Items added
    ↓
processPayment(orderId, PaymentMethod.UPI)   ← customer chooses here
    ↓
UPIPaymentGateway.processPayment(...)
```

---

### Issue 3 — Thread-safe Payment ID Generation

`PaymentGateway.processPayment()` needs to create a new `Payment` with a unique ID. Use `AtomicInteger` for safe, sequential ID generation without synchronisation overhead:

```java
private static final AtomicInteger PAYMENT_ID_GENERATOR = new AtomicInteger(1);

// Inside processPayment():
int paymentId = PAYMENT_ID_GENERATOR.getAndIncrement();
Payment payment = new Payment(paymentId, order.getOrderId(), amount, paymentMethod);
```

---

## Backend Thinking — API Design

When this system is extended into a REST API, the endpoints map naturally to the service methods:

**Write Operations:**
```
POST /orders                        → createOrder()
POST /orders/{id}/items             → addProduct()
POST /orders/{id}/payment           → processPayment()
POST /orders/{id}/confirm           → confirmOrder()
POST /orders/{id}/cancel            → cancelOrder()
```

**Read Operations:**
```
GET /orders/{id}                    → getOrder()
GET /orders?customerId=10           → findByCustomer()
GET /orders?status=CONFIRMED        → findByStatus()
```

**Layered Architecture:**
```
Controller   ← handles HTTP request/response
    ↓
Service      ← business logic, orchestration
    ↓
Repository   ← data access
    ↓
Database     ← persistence
```

---

### 🔍 Real-World Problem: What if payment succeeds externally but our app crashes before saving?

**Scenario:**
```
processPayment()
    ↓
Gateway charges the customer ✅
    ↓
App crashes 💥
    ↓
Payment not saved to PaymentRepository
Order status still PAYMENT_PENDING
```

**This introduces several important backend concepts:**

| Concept | Description |
|---|---|
| **Idempotency** | Retrying the same payment request should not charge the customer twice — each request needs a unique idempotency key |
| **Transaction boundaries** | Payment save + order status update must succeed or fail together — partial updates are dangerous |
| **Retries with deduplication** | The gateway should recognise a duplicate request and return the original result instead of processing again |
| **Eventual consistency** | Accept that the system may be temporarily inconsistent after a crash, but guarantee it converges to a correct state |

> 📄 These concepts are highly relevant for Java backend interviews and will be explored further in advanced topics.