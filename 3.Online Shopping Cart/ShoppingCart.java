import java.util.*;

class ShoppingCart {
    private final Map<Integer, CartItem> items;
    private final DiscountStrategy discountStrategy;

    public ShoppingCart(DiscountStrategy discountStrategy) {
        if (discountStrategy == null) {
            throw new IllegalArgumentException(
                    "Discount strategy cannot be null.");
        }

        this.items = new HashMap<>();
        this.discountStrategy = discountStrategy;
    }

    public void addItem(Product product, int quantity) {
        if(quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        // find the id of the product
        int productId = product.getProductId();
        // find the existing item in the cart (if any)
        CartItem existingItem = items.get(productId);

        // check if the product already exists in the cart
        if (existingItem != null) {
            // update the quantity of the existing item
            existingItem.updateQuantity(
                    existingItem.getQuantity() + quantity);

        } else {
            // add a new item to the cart
            items.put(
                    productId,
                    new CartItem(product, quantity)
            );
        }
    }

    public boolean removeItem(int productId) {
        // remove the item from the cart and return true if it was present, false otherwise
        return items.remove(productId) != null;
    }

    public boolean updateItemQuantity(int productId, int newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        // check if the item exists in the cart
        if (items.containsKey(productId)) {
            // update the quantity of the existing item
            CartItem existingItem = items.get(productId);
            existingItem.updateQuantity(newQuantity);
            return true;
        }
        return false;
    }

    public double getSubtotal() {
        // calculate the subtotal of all items in the cart
        return items.values().stream()
                .mapToDouble(CartItem::getItemTotal)
                .sum();
    }

    public double getDiscount() {
        return discountStrategy.calculateDiscount(items.values());
    }

    public double getTotal() {
        // calculate the total after applying the discount
        return getSubtotal() - getDiscount();
    }

    public int getTotalQuantity() {
        // calculate the total quantity of all items in the cart
        return items.values().stream()
            .mapToInt(CartItem::getQuantity)
            .sum();
    }

    public void clearCart() {
        items.clear();
    }

    public List<Product> getProductsByCategory(Category category) {
        return items.values().stream()
                .filter(item -> item.getProduct().getProductCategory() == category)
                .map(CartItem::getProduct)
                .toList();
    }

    public List<Product> getProductsAbovePrice(double price) {
        return items.values().stream()
                .filter(item -> item.getProduct().getProductPrice() > price)
                .map(CartItem::getProduct)
                .toList();
    }

    public Optional<Product> getMostExpensiveProduct() {
        return Optional.ofNullable(items.values().stream()
                .max(Comparator.comparingDouble(item -> item.getProduct().getProductPrice()))
                .map(CartItem::getProduct)
                .orElse(null));
        
        /* return items.values()
            .stream()
            .max(Comparator.comparingDouble(
                    item -> item.getProduct().getProductPrice()
            ))
            .map(CartItem::getProduct); */
    }

    int generateOrderId() {
        // generate a unique order ID (for simplicity, using current timestamp)
        return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
    }

    Order checkout() {
        double subtotal = getSubtotal();
        double discount = getDiscount();
        double finalAmount = subtotal - discount;

        List<OrderItem> orderItems = items.values().stream()
                .map(item -> new OrderItem(item.getProduct(), item.getQuantity()))
                .toList();

        int orderId = generateOrderId();
           
        return new Order(orderId, orderItems, subtotal, discount, finalAmount);
    }
}