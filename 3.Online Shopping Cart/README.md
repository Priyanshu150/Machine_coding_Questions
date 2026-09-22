# 🛒 Online Shopping Cart

> **Level 2 — Collections + Java Features**

An in-memory Online Shopping Cart system built using core Java, demonstrating the **Strategy Design Pattern**, **Separation of Concerns**, and clean use of Java Collections and Functional interfaces.

---

## 📌 Table of Contents

- [Problem Overview](#problem-overview)
- [Initial Design (v1)](#initial-design-v1)
- [Design Issues & Improvements](#design-issues--improvements)
- [Improved Design (v2)](#improved-design-v2)
  - [Enums](#enums)
  - [Product](#product)
  - [CartItem](#cartitem)
  - [DiscountStrategy](#discountstrategy)
  - [ShoppingCart](#shoppingcart)
  - [Order](#order)
- [Key Design Notes](#key-design-notes)

---

## Problem Overview

Design and implement an **Online Shopping Cart** that allows users to add/remove/update products, apply discount strategies, and check out to produce an order — all in-memory, no database or UI.

---

## Initial Design (v1)

A first-pass design before refinement.

### `Cart`
```
product, quantity
itemTotal = product.price * quantity

List<Product> getProductsByCategory(Category category)
Optional<Product> getMostExpensiveProduct()
```

### `DiscountStrategy` (interface — v1)
```
if (discountType == PERCENTAGE) { ... }
else if (discountType == FLAT)  { ... }
else if (discountType == CATEGORY) { ... }
else { discount = 0; }
```
> ⚠️ All discount logic crammed into one if-else chain inside one class — not extensible.

### `ShoppingCart implements DiscountStrategy`

| Method | Description |
|---|---|
| `addProduct(Product, int quantity)` | Add new product or update quantity if already present |
| `removeProduct(int productId)` | Remove product (must exist) |
| `updateQuantity(int productId, int quantity)` | Update if `quantity > 0`; reject if `<= 0` |
| `getSubtotal()` | Sum of all item totals |
| `getDiscount()` | Calculated discount amount |
| `getTotal()` | `subtotal - discount` |
| `clearCart()` | Clear all items |

### `Order`
```
orderId, items, subTotal, discount, finalTotal, orderStatus

boolean checkout()
boolean confirm()
boolean cancel()
```

---

## Design Issues & Improvements

| # | Issue | Fix |
|---|---|---|
| 1 | `Cart` mixes product data with cart item behaviour | Split into `CartItem` class with its own `getItemTotal()` |
| 2 | All discount types handled in one if-else block | Each discount type becomes its own class implementing `DiscountStrategy` |
| 3 | `ShoppingCart implements DiscountStrategy` — wrong relationship | `ShoppingCart` **has-a** `DiscountStrategy`, not **is-a** |
| 4 | `boolean clearCart()` — no meaningful boolean to return | Change to `void clearCart()` |
| 5 | Query methods (`getProductsByCategory`) belong to `Cart`, not `Product` | Move all functional queries to `ShoppingCart` |
| 6 | `Order.items` directly references `CartItem` | `Order` should have its own `OrderItem` snapshot — cart and order items must not be coupled |
| 7 | `Order.checkout()` lives inside `Order` | `checkout()` should belong to `ShoppingCart` — it owns the cart state |

---

## Improved Design (v2)

### Enums

```java
enum Category {
    ELECTRONICS,
    CLOTHING,
    BOOKS,
    GROCERY
}

enum OrderStatus {
    CREATED,
    CONFIRMED,
    CANCELLED
}
```

---

### `Product`

```java
public final class Product {
    private final int productId;
    private final String name;
    private final double price;
    private final Category category;
}
```

> Fields are `final` — a product's identity and price should not change after creation.

---

### `CartItem`

Replaces the raw `(product, quantity)` pair in v1. Encapsulates item-level logic cleanly.

```java
public class CartItem {
    private Product product;
    private int quantity;

    public double getItemTotal() {
        return product.getPrice() * quantity;
    }
}
```

---

### `DiscountStrategy`

Each discount type is its own class. Adding a new discount type requires **no changes** to existing code.

```java
interface DiscountStrategy {
    double calculateDiscount(Collection<CartItem> items);
}
```

```java
class PercentageDiscount implements DiscountStrategy {
    private final double percentage;

    @Override
    public double calculateDiscount(Collection<CartItem> items) {
        double subtotal = items.stream()
            .mapToDouble(CartItem::getItemTotal).sum();
        return subtotal * (percentage / 100);
    }
}

class FlatDiscount implements DiscountStrategy {
    private final double flatAmount;

    @Override
    public double calculateDiscount(Collection<CartItem> items) {
        return flatAmount;
    }
}

class CategoryDiscount implements DiscountStrategy {
    private final Category category;
    private final double percentage;

    @Override
    public double calculateDiscount(Collection<CartItem> items) {
        return items.stream()
            .filter(item -> item.getProduct().getCategory() == category)
            .mapToDouble(CartItem::getItemTotal).sum() * (percentage / 100);
    }
}
```

**Usage:**

```java
ShoppingCart cart = new ShoppingCart(new PercentageDiscount(10));
ShoppingCart cart = new ShoppingCart(new FlatDiscount(200));
ShoppingCart cart = new ShoppingCart(new CategoryDiscount(Category.ELECTRONICS, 15));
```

---

### `ShoppingCart`

`ShoppingCart` **has-a** `DiscountStrategy` — it delegates discount calculation rather than implementing it.

```java
class ShoppingCart {
    private final Map<Integer, CartItem> items;       // productId → CartItem
    private final DiscountStrategy discountStrategy;
}
```

**Cart Operations:**

| Method | Return Type | Description |
|---|---|---|
| `addProduct(Product, int quantity)` | `boolean` | Add new item or update quantity if already present |
| `removeProduct(int productId)` | `boolean` | Remove item; product must exist |
| `updateQuantity(int productId, int quantity)` | `boolean` | Update if `quantity > 0`; reject if `<= 0` |
| `void clearCart()` | `void` | Clear all cart items |

**Pricing:**

| Method | Return Type | Description |
|---|---|---|
| `getSubtotal()` | `double` | Sum of all `CartItem.getItemTotal()` |
| `getDiscount()` | `double` | Delegated to `discountStrategy.calculateDiscount(items)` |
| `getTotal()` | `double` | `subtotal - discount` |
| `getTotalQuantity()` | `int` | Sum of quantities across all items |

**Functional Queries:**

| Method | Return Type | Description |
|---|---|---|
| `getProductsByCategory(Category)` | `List<Product>` | Filter items by category |
| `getProductsAbovePrice(double price)` | `List<Product>` | Filter items above a price threshold |
| `getMostExpensiveProduct()` | `Optional<Product>` | Return the highest-priced product in cart |

**Checkout:**

| Method | Return Type | Description |
|---|---|---|
| `checkout()` | `Order` | Creates and returns an `Order` snapshot from current cart state |

> `checkout()` belongs to `ShoppingCart` because it owns the cart state. `Order` should not reach into the cart.

---

### `Order`

`Order` holds an **independent snapshot** of the cart at the time of checkout. It must not hold references to `CartItem` — changes to the cart after checkout should not affect the order.

```java
public class Order {
    private final int orderId;
    private final List<OrderItem> items;      // snapshot — NOT CartItem
    private final double subTotal;
    private final double discount;
    private final double finalTotal;
    private OrderStatus status;

    boolean confirm();
    boolean cancel();
}
```

---

## Key Design Notes

**1. Strategy Pattern for Discounts**

Instead of one if-else chain, each discount type is a separate class. `ShoppingCart` accepts any `DiscountStrategy` via its constructor — this is the **Strategy Design Pattern** and follows the **Open/Closed Principle**.

**2. Composition over Inheritance**

```
✅ ShoppingCart HAS-A DiscountStrategy   (composition)
❌ ShoppingCart IS-A  DiscountStrategy   (inheritance — wrong)
```

**3. Cart ≠ Order Items**

`CartItem` and `OrderItem` are intentionally separate. The `Order` is a **point-in-time snapshot**. If the cart is cleared or modified after checkout, the order must remain unchanged.

**4. `final` Fields on `Product`**

A product's `id`, `name`, and `price` should never change after creation — making fields `final` enforces this at the compiler level.

**5. `Map<Integer, CartItem>` over `List<CartItem>`**

Using `productId` as the key gives O(1) lookup for `addProduct`, `removeProduct`, and `updateQuantity`, versus O(n) linear scan with a `List`.

---

---

## ❓ Interview Q&A

### Q1. Should `Product` price be immutable? Why?

**Answer:**

In this in-memory design, yes — `price` is `final` and immutable. Since there is no database or external price feed, there is no mechanism to propagate a price change safely across items already in carts or orders. Making it immutable keeps behaviour predictable.

In the real world, however, price **should be mutable** on the catalog. The key insight is:

> **Product identity should be stable; pricing can change over time, but historical transactions need their own price snapshot.**

This is exactly why `OrderItem` stores its own price at the time of checkout — even if the catalog price changes later, past orders remain accurate. The `Product` holds the *current* catalog price; the `OrderItem` holds the *agreed* price.

---

### Q2. Should `CartItem` store the price separately, or always read `product.getPrice()`?

**Answer:**

`CartItem` should always read `product.getPrice()` to reflect the latest catalog price. Since the cart is a live, mutable session, it should show the current price up until checkout.

The price gets **locked in** only when `checkout()` is called and an `OrderItem` is created. This is the correct place for a price snapshot.

The full price lifecycle looks like this:

```
Product
└── price = current catalog price
         │
         ▼
    CartItem
    └── reads product.getPrice() dynamically
         │
         ▼
    checkout()
         │
         ▼
    OrderItem
    └── stores agreed price at time of purchase  ← price locked here
         │
         ▼
    Order
    └── financial values are stable and immutable
```

This separation means:
- Cart always shows the **up-to-date price**
- Orders always reflect the **price the customer agreed to pay**

