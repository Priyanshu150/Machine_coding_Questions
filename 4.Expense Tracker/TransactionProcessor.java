import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.function.Consumer;
import java.util.function.Supplier;

class TransactionProcessor {
    private final TransactionRepository repository;
    
    // constructor injection for the repository dependency
    public TransactionProcessor(TransactionRepository repository) {
        this.repository = repository;
    }

    // ---------------------------------------------------------
    // Functional Operations
    // ---------------------------------------------------------

    /**
     * Predicate:
     * ExpenseTransaction -> boolean
     *
     * Filters transactions based on a caller-provided condition.
     */
    List<ExpenseTransaction> filter(Predicate<ExpenseTransaction> condition) {
        List<ExpenseTransaction> filteredTransactions = new ArrayList<>();
        // iterate through all transactions and apply the condition
        for (ExpenseTransaction transaction : repository.getAllTransactions()) {
            if (condition.test(transaction)) {
                filteredTransactions.add(transaction);
            }
        }
        return filteredTransactions;
    }

    /**
     * Function:
     * ExpenseTransaction -> R
     *
     * Transforms each transaction into another type/value.
     */
    <R> List<R> transform(Function<ExpenseTransaction, R> mapper) {
        return repository.getAllTransactions().stream()
            .map(mapper)
            .toList();
    }

    /**
     * Consumer:
     * ExpenseTransaction -> void
     *
     * Performs an action on every transaction.
     */
    void processEach(Consumer<ExpenseTransaction> action) {
        // apply the action to each transaction in the repository
        repository.getAllTransactions().forEach(action);
    }

    /**
     * Supplier:
     * () -> ExpenseTransaction
     *
     * Creates/supplies an ExpenseTransaction.
     */
    ExpenseTransaction createExpenseTransaction(Supplier<ExpenseTransaction> supplier) {
        return supplier.get();
    }

    /**
     * Comparator:
     * Defines how transactions should be ordered.
     */
    List<ExpenseTransaction> sort(Comparator<ExpenseTransaction> comparator) {
        return repository.getAllTransactions().stream()
            .sorted(comparator)
            .toList();
    }

    // ---------------------------------------------------------
    // Business Operations
    // ---------------------------------------------------------

    // get all the transaction which are approved using functional filter operation  
    List<ExpenseTransaction> getApprovedTransactions() {
        return filter(
            t -> t.getStatus() == TransactionStatus.APPROVED
        );
    }

    // get all the transaction which are pending using functional filter operation  
    List<ExpenseTransaction> getPendingTransactions() {
        return filter(
            t -> t.getStatus() == TransactionStatus.PENDING
        );
    }

    // get all transactions above a certain amount
    List<ExpenseTransaction> getTransactionAbove(double amount) {
        return filter( t -> t.getAmount() > amount );
    }

    double getTotalApprovedAmount() {
        return getApprovedTransactions().stream()
            .mapToDouble(ExpenseTransaction::getAmount)
            .sum();
    }

    // get the highest expense transaction
    Optional<ExpenseTransaction> getHighestExpense() {
        return repository.getAllTransactions().stream()
            .max(Comparator.comparingDouble(ExpenseTransaction::getAmount));
    }

    /**
     * Approved expenses grouped by department
     * with the total amount for each department.
     */
    Map<Department, Double> groupExpensesByDepartment() {
        return repository.getAllTransactions().stream()
                .filter(t -> t.getStatus() == TransactionStatus.APPROVED)
                .collect(Collectors.groupingBy(
                    t -> t.getEmployee().getDepartment(),
                    Collectors.summingDouble(
                        ExpenseTransaction::getAmount
                    )
                ));
    }

    /**
     * Group transactions by expense category.
     */
    Map<ExpenseCategory, List<ExpenseTransaction>> groupByCategory() {
        return repository.getAllTransactions().stream()
            .collect(Collectors.groupingBy(ExpenseTransaction::getExpenseCategory));
    }

    // get transactions by department
    List<ExpenseTransaction> getTransactionsByDepartment(Department department) {
        return filter(t -> t.getEmployee().getDepartment() == department);
    }

    // get transactions by employee
    List<ExpenseTransaction> getTransactionsByEmployee(Employee employee) {
        return filter(t -> t.getEmployee().equals(employee));   
    }
}