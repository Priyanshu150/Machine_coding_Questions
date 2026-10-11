import java.math.BigDecimal;

class PaypalPaymentGateway implements PaymentGateway {
    @Override
    public Payment processPayment(Order order, PaymentMethod paymentMethod) { 
        int paymentId = PAYMENT_ID_GENERATOR.getAndIncrement();
        BigDecimal amount = order.calculateTotal();
        Payment payment = new Payment(paymentId, order.getOrderId(), amount, paymentMethod);

        try{
            // Simulate payment processing logic
            // For simplicity, we assume the payment is always successful
            payment.markSuccess();
            System.out.println("Successfully processed PayPal payment for Order ID: " + order.getOrderId());
        } catch (Exception e) {
            payment.markFailed();
            System.out.println("Failed to process PayPal payment for Order ID: " + order.getOrderId());
        }
        return payment;
    }
}