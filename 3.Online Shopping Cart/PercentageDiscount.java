import java.util.*;

class PercentageDiscount implements DiscountStrategy{
    private final double percentage;

    public PercentageDiscount(double percentage) {
        if(percentage < 0 || percentage > 1) {
            throw new IllegalArgumentException("Percentage must be between 0 and 1.");
        }
        this.percentage = percentage;
    }

    @Override
    public double calculateDiscount(Collection<CartItem> cartItems) {
        double subtotal = cartItems.stream()
                .mapToDouble(item -> item.getItemTotal())
                .sum();
        return subtotal * percentage;
    }   
}