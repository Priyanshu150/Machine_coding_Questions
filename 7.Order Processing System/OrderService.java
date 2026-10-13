import java.util.*;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger; 
import java.util.function.Predicate;

class OrderService {
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final Map<PaymentMethod, PaymentGateway> gateways;
    private static final AtomicInteger ORDER_ID_GENERATOR = new AtomicInteger(1);

    public OrderService(OrderRepository orderRepository, PaymentRepository paymentRepository, Map<PaymentMethod, PaymentGateway> gateways) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.gateways = gateways;
    }

    List<Order> filter(Predicate<Order> predicate) {
        return orderRepository.findAll().stream()
                .filter(predicate)
                .toList();
    }

    public Order createOrder(Customer customer) {
        int orderId = ORDER_ID_GENERATOR.getAndIncrement();
        Order order = new Order(orderId, customer);

        if(orderRepository.save(order)) {
            System.out.println("Order created with ID: " + orderId);
            return order;
        } else {
            throw new RuntimeException("Failed to create order. Order ID already exists: " + orderId);
        }
    }

    public Optional<Order> getOrder(int orderId) {
        return orderRepository.findById(orderId);
    }

    public void addProduct(int orderId, Product product, int quantity) {
        // Implementation for adding product to order
        Optional<Order> optionalOrder = getOrder(orderId);

        if(optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            if(order.addItem(product, quantity)) {
                System.out.println("Added " + quantity + " of " + product.getName() + " to Order ID: " + orderId);
            } else {
                throw new RuntimeException("Failed to add product to order. Order ID: " + orderId);
            }
        } else {
            throw new RuntimeException("Order not found with ID: " + orderId);
        }
    }

    public BigDecimal calculateTotal(int orderId) {
        Optional<Order> optionalOrder = getOrder(orderId);
        if(optionalOrder.isPresent()) {
            return optionalOrder.get().calculateTotal();
        } else {
            throw new RuntimeException("Order not found with ID: " + orderId);
        }
    }

    public Payment processPayment(int orderId, PaymentMethod method) {
        Optional<Order> optionalOrder = getOrder(orderId);

        if(optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            PaymentGateway gateway = gateways.get(method);
            if(gateway != null) {
                Payment payment = gateway.processPayment(order, method);
                paymentRepository.save(payment);
                return payment;
            } else {
                throw new RuntimeException("Payment method not supported: " + method);
            }
        } else {
            throw new RuntimeException("Order not found with ID: " + orderId);
        }
    }

    // method to confirm an order
    public void confirmOrder(int orderId){
        Optional<Order> optionalOrder = getOrder(orderId);
        if(optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            try {
                order.confirm();
                System.out.println("Order ID: " + orderId + " confirmed.");
            } catch (IllegalStateException e) {
                throw new RuntimeException("Failed to confirm order. " + e.getMessage());
            }
        }else{
            throw new RuntimeException("Order not found with ID: " + orderId);
        }
    }

    // method to cancel an order
    public void cancelOrder(int orderId) {
        Optional<Order> optionalOrder = getOrder(orderId);
        if(optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            try{
                order.cancel();
                System.out.println("Order ID: " + orderId + " cancelled.");
            } catch (IllegalStateException e) {
                throw new RuntimeException("Failed to cancel order. " + e.getMessage());
            }
        } else {
            throw new RuntimeException("Order not found with ID: " + orderId);
        }
    }

    Payment retryPayment(int orderId, PaymentMethod method) {
        return processPayment(orderId, method);
    }

    List<Order> findByCustomer(int customerId) {
        return filter(order -> order.getCustomerId() == customerId);
    }

    List<Order> findByStatus(OrderStatus status) {
        return filter(order -> order.getStatus() == status);
    }

    List<Order> findOrdersAboveAmount(BigDecimal amount) {
        return filter(order -> order.calculateTotal().compareTo(amount) > 0);
    }

    Map<OrderStatus, Long> countOrdersByStatus() {
        return orderRepository.findAll().stream()
                .collect(Collectors.groupingBy(Order::getStatus, Collectors.counting()));
    }

    BigDecimal totalConfirmedOrderValue(){
        return orderRepository.findAll().stream()
                .filter(order -> order.getStatus() == OrderStatus.CONFIRMED)
                .map(Order::calculateTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    Optional<Order> highestValueOrder(){
        return orderRepository.findAll().stream()
                .max(Comparator.comparing(Order::calculateTotal));
    }
}