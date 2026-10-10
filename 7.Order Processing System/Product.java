
import java.math.BigDecimal;

class Product {
    private final int productId;
    private final String name;
    private final BigDecimal price;

    public Product(int productId, String name, BigDecimal price) {
        this.productId = productId;
        this.name = name;
        this.price = price;
    }

    public int getProductId() {
        return productId;
    }
    
    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(productId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Product product = (Product) obj;
        return productId == product.productId;
    }
}