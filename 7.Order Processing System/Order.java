import java.util.*;
import java.math.BigDecimal;

class Order {
    private final int orderId;
    private final Customer customer;
    private final List<OrderItem> items;
    private OrderStatus status;         // mutable — guarded transitions

    public Order(int orderId, Customer customer) {
        this.orderId = orderId;
        this.customer = customer;
        this.items = new ArrayList<>();
        this.status = OrderStatus.CREATED; // initial status
    }

    public int getOrderId() {
        return orderId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public boolean addItem(Product product, int quantity) { 
        if(quantity <= 0) {
            return false; // invalid quantity
        }

        if(status == OrderStatus.PAID) {
            return false;  // cannot add items to a paid order
        }

        OrderItem newItem = new OrderItem(product, quantity);

        if(items.contains(newItem)) {
            // If the product already exists in the order, update the quantity
            for(OrderItem existingItem : items) {
                if(existingItem.getProduct().equals(product)) {
                    existingItem.setQuantity(existingItem.getQuantity() + quantity); // update quantity
                    return true;
                }
            }
        }
        
        // if the product does not exist in the order, add it as a new item
        items.add(newItem);
        return true;
    }

    public BigDecimal calculateTotal() {
        return items.stream()
                    .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // CREATED → PAYMENT_PENDING
    public boolean startPayment(){
        if(status != OrderStatus.CREATED) {
            throw new IllegalStateException("Payment can only be started from CREATED status.");
        }

        status = OrderStatus.PAYMENT_PENDING;
        return true;
    }

    // PAYMENT_PENDING → PAID
    public boolean paymentSuccessful() {
        if(status != OrderStatus.PAYMENT_PENDING) {
            throw new IllegalStateException("Payment can only be marked as successful from PAYMENT_PENDING status.");
        }
        status = OrderStatus.PAID;
        return true;
    }

    // PAYMENT_PENDING → PAYMENT_FAILED
    public boolean paymentFailed(){
        if(status != OrderStatus.PAYMENT_PENDING) {
            throw new IllegalStateException("Payment can only be marked as failed from PAYMENT_PENDING status.");
        }
        status = OrderStatus.PAYMENT_FAILED;
        return true;
    }

    // PAID → CONFIRMED
    public boolean confirm() {
        if(status != OrderStatus.PAID) {
            throw new IllegalStateException("Order can only be confirmed from PAID status.");
        }
        status = OrderStatus.CONFIRMED;
        return true;
    }

    // CREATED / PAYMENT_FAILED → CANCELLED
    public boolean cancel() {
        if(status != OrderStatus.CREATED && status != OrderStatus.PAYMENT_FAILED) {
            throw new IllegalStateException("Order can only be cancelled from CREATED or PAYMENT_FAILED status.");
        }
        status = OrderStatus.CANCELLED;
        return true;
    }
}
