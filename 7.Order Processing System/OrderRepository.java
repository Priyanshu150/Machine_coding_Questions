import java.util.*;

class OrderRepository {
    private final Map<Integer, Order> orderMap;     // orderId → Order

    public OrderRepository() {
        this.orderMap = new HashMap<>();
    }

    public boolean save(Order order){
        if(orderMap.containsKey(order.getOrderId())) {
            return false; // order with this ID already exists
        }

        orderMap.put(order.getOrderId(), order);
        return true;
    }

    public Optional<Order> findById(int orderId){ 
        return Optional.ofNullable(orderMap.get(orderId));
    }

    public boolean delete(int orderId){ 
        return orderMap.remove(orderId) != null;
    }
    
    public List<Order> findAll(){
        return new ArrayList<>(orderMap.values());
    }
}