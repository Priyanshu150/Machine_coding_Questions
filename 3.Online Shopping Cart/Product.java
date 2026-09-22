

class Product{
    private final int productId;
    private final String productName;
    private final double productPrice;
    private final Category productCategory;


    public Product(int productId, String productName, double productPrice, Category productCategory) {
        if(productPrice < 0){
            throw new IllegalArgumentException("Product price cannot be negative");
        }

        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productCategory = productCategory;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public Category getProductCategory() {
        return productCategory;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", productPrice=" + productPrice +
                ", productCategory=" + productCategory +
                '}';
    }
}   