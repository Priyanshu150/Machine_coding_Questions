import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.function.Consumer;
import java.util.function.Supplier;

class TransactionProcessor {
    private final TransactionRepository repository;

    public TransactionProcessor(TransactionRepository repository) {
        this.repository = repository;
    }

    List<ExpenseTransaction> filter(Predicate<ExpenseTransaction> predicate) {
        List<ExpenseTransaction> filteredTransactions = new ArrayList<>();
        for (ExpenseTransaction transaction : repository.getAllTransactions()) {
            if (predicate.test(transaction)) {
                filteredTransactions.add(transaction);
            }
        }
        return filteredTransactions;
    }

    <R> List<R> transform(Function<ExpenseTransaction, R> mapper) {
        return repository.getAllTransactions().stream()
            .map(mapper)
            .toList();
    }

    void processEach(Consumer<ExpenseTransaction> action) {
        repository.getAllTransactions().forEach(action);
    }

    ExpenseTransaction createDefault(Supplier<ExpenseTransaction> supplier) {
        return supplier.get();
    }

     List<ExpenseTransaction> getApprovedTransactions() {
        return filter(
            t -> t.getStatus() == TransactionStatus.APPROVED
        );
    }

    List<ExpenseTransaction> getPendingTransactions() {
        return filter(
            t -> t.getStatus() == TransactionStatus.PENDING
        );
    }

    List<ExpenseTransaction> getTransactionAbove(double amount) {
        return filter( t -> t.getAmount() > amount );
    }

    double getTotalApprovedAmount() {
        return getApprovedTransactions().stream()
            .mapToDouble(ExpenseTransaction::getAmount)
            .sum();
    }

    Optional<ExpenseTransaction> getHighestExpense() {
        return repository.getAllTransactions().stream()
            .max(Comparator.comparingDouble(ExpenseTransaction::getAmount));
    }

    Map<Department, List<ExpenseTransaction>> groupByDepartment() {
        return repository.getAllTransactions().stream()
            .collect(Collectors.groupingBy(t -> t.getEmployee().getDepartment()));
    }

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

    Map<ExpenseCategory, List<ExpenseTransaction>> groupByCategory() {
        return repository.getAllTransactions().stream()
            .collect(Collectors.groupingBy(ExpenseTransaction::getExpenseCategory));
    }

    Map<Employee, List<ExpenseTransaction>> groupByEmployeeId() {
        return repository.getAllTransactions().stream()
            .collect(Collectors.groupingBy(ExpenseTransaction::getEmployee::getId));
    }

    List<ExpenseTransaction> sortByAmount(Comparator<ExpenseTransaction> comparator) {
        return repository.getAllTransactions().stream()
            .sorted(comparator)
            .toList();
    }

    List<ExpenseTransaction> getTransactionsByDepartment(Department department) {
        return repository.getAllTransactions().stream()
            .filter(t -> t.getEmployee().getDepartment() == department)
            .toList();
    }

    List<ExpenseTransaction> getTransactionsByEmployee(Employee employee) {
        return repository.getAllTransactions().stream()
            .filter(t -> t.getEmployee().equals(employee))
            .toList();
    }
}