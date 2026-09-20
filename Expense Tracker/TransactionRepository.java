import java.util.*;

class TransactionRepository {
    private final Map<Integer, ExpenseTransaction> store;       // transactionId → ExpenseTransaction

    public TransactionRepository() {
        this.store = new HashMap<>();
    }

    boolean addTransaction(ExpenseTransaction transaction) {
        if (store.containsKey(transaction.getTransactionId())) {
            return false;   // transactionId already exists
        }
        store.put(transaction.getTransactionId(), transaction);
        return true;
    }

    boolean removeTransaction(int transactionId) {
        return store.remove(transactionId) != null;
    }

    Optional<ExpenseTransaction> findTransaction(int transactionId) {
        return Optional.ofNullable(store.get(transactionId));
    }

    List<ExpenseTransaction> getAllTransactions() {
        return new ArrayList<>(store.values());
    }
}