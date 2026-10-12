import java.math.BigDecimal;

class OrderItem {
    private final Product product;
    private int quantity;               // not final — quantity can be updated if same product added again
    private final BigDecimal unitPrice; // snapshot of price at time of adding

    public OrderItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = product.getPrice(); // capture the price at the time of adding
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
}