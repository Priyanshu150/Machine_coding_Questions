import java.util.*;

class CategoryDiscount implements DiscountStrategy {
    private final Category category;
    private final double percentage;

    public CategoryDiscount(Category category, double percentage) {
        if(percentage < 0 || percentage > 1) {
            throw new IllegalArgumentException("Percentage must be between 0 and 1.");
        }
        this.category = category;
        this.percentage = percentage;
    }

    @Override
    public double calculateDiscount(Collection<CartItem> cartItems) {
        return cartItems.stream()
                .filter(item -> item.getProduct().getProductCategory() == this.category)
                .mapToDouble(CartItem::getItemTotal)
                .sum() * this.percentage;
    }
}