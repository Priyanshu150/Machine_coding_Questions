
class Customer {
    private final int customerId;
    private final String name;
    private final String email;
    
    public Customer(int customerId, String name, String email) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public int hashCode() { 
        return Integer.hashCode(customerId);
     }

    @Override
    public boolean equals(Object obj) { 
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Customer customer = (Customer) obj;
        return customerId == customer.customerId;
    }
}