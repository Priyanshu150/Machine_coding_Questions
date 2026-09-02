import java.util.*;

class FlatDiscount implements DiscountStrategy{
    private final double amount;

    public FlatDiscount(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Discount amount cannot be negative.");
        }   
        this.amount = amount;
    }

    @Override
    public double calculateDiscount(Collection<CartItem> cartItems) {
        double subtotal = cartItems.stream()
                .mapToDouble(item -> item.getItemTotal())
                .sum();
        
        return Math.min(subtotal, amount);
    }
}