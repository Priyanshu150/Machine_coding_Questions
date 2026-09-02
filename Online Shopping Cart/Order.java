import java.util.*;

public class Order {
    private final int orderId;
    private final List<OrderItem> items;
    private final double subtotal;
    private final double discount;
    private final double finalAmount;
    private OrderStatus status;

    protected Order(int orderId, List<OrderItem> items, double subtotal, double discount, double finalAmount) {
        this.orderId = orderId;
        this.items = List.copyOf(items);
        this.subtotal = subtotal;
        this.discount = discount;
        this.finalAmount = finalAmount;
        this.status = OrderStatus.CREATED;
    }

    public int getOrderId() {
        return orderId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getDiscount() {
        return discount;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void confirm() {
        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException("Only CREATED orders can be confirmed.");
        }

        status = OrderStatus.CONFIRMED;
    }

    public void cancel() {
        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException("Only CREATED orders can be cancelled.");
        }

        status = OrderStatus.CANCELLED;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", items=" + items +
                ", subtotal=" + subtotal +
                ", discount=" + discount +
                ", finalAmount=" + finalAmount +
                ", status=" + status +
                '}';
    }
}