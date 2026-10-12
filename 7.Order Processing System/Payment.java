import java.math.BigDecimal;

class Payment {
    private final int paymentId;
    private final int orderId;
    private final BigDecimal amount;
    private final PaymentMethod paymentMethod;
    private PaymentStatus status;       // mutable — INITIATED → SUCCESS / FAILED

    public Payment(int paymentId, int orderId, BigDecimal amount, PaymentMethod paymentMethod) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = PaymentStatus.INITIATED; // initial status
    }

    public int getPaymentId() {
        return paymentId;
    }

    public int getOrderId() {
        return orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void markSuccess() { 
        if(status != PaymentStatus.INITIATED) {
            throw new IllegalStateException("Payment can only be marked as SUCCESS from INITIATED state.");
        }
        status = PaymentStatus.SUCCESS;
    }

    public void markFailed()  { 
        if(status != PaymentStatus.INITIATED) {
            throw new IllegalStateException("Payment can only be marked as FAILED from INITIATED state.");
        }
        status = PaymentStatus.FAILED;
    }
}