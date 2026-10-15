import java.util.*;

class PaymentRepository {
    private final Map<Integer, Payment> paymentMap;  // paymentId → Payment

    public PaymentRepository() {
        this.paymentMap = new HashMap<>();
    }

    public boolean save(Payment payment){
        if(paymentMap.containsKey(payment.getPaymentId())) {
            return false; // payment with this ID already exists
        }

        paymentMap.put(payment.getPaymentId(), payment);
        return true;
    }

    public Optional<Payment> findById(int paymentId){
        return Optional.ofNullable(paymentMap.get(paymentId));
    }

    public Optional<Payment> findByOrderId(int orderId) {
        return paymentMap.values().stream()
                .filter(payment -> payment.getOrderId() == orderId)
                .findFirst();
    }

    public List<Payment> findAll() {
        return new ArrayList<>(paymentMap.values());
    }
}