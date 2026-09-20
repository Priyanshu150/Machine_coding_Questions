
class CartItem {
    private Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        if(quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getItemTotal() {
        return product.getProductPrice() * quantity;
    }

    public void updateQuantity(int newQuantity) {
        if(newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        this.quantity = newQuantity;
    }
}

