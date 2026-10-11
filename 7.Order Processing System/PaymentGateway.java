import java.util.concurrent.atomic.AtomicInteger;

interface PaymentGateway{
    static final AtomicInteger PAYMENT_ID_GENERATOR = new AtomicInteger(1);
    
    Payment processPayment(Order order, PaymentMethod paymentMethod);
}