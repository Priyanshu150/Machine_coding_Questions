import java.util.*;

interface DiscountStrategy {
    double calculateDiscount(Collection<CartItem> cartItems);
}