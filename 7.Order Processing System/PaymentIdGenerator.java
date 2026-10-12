import java.util.concurrent.atomic.AtomicInteger;

class PaymentIdGenerator {

    private static final AtomicInteger COUNTER = new AtomicInteger(1);

    public static int nextId() {
        return COUNTER.getAndIncrement();
    }
}