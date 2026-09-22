
public class ExpenseTransaction {
    private final int transactionId;
    private final Employee employee;
    private final double amount;
    private final ExpenseCategory expenseCategory;
    private TransactionStatus status;   // mutable — PENDING → APPROVED / REJECTED


    public ExpenseTransaction(int transactionId, Employee employee, double amount, ExpenseCategory expenseCategory) {
        this.transactionId = transactionId;
        this.employee = employee;
        this.amount = amount;
        this.expenseCategory = expenseCategory;
        this.status = TransactionStatus.PENDING;    // default status
    }

    public int getTransactionId() {
        return transactionId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public double getAmount() {
        return amount;
    }

    public ExpenseCategory getExpenseCategory() {
        return expenseCategory;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public boolean approve() {
        if(status != TransactionStatus.PENDING) {
            throw new IllegalStateException("Transaction is not in PENDING state. Current status: " + status);
        }
        status = TransactionStatus.APPROVED;
        return true;
    }

    public boolean reject() {
        if(status != TransactionStatus.PENDING) {
            throw new IllegalStateException("Transaction is not in PENDING state. Current status: " + status);
        }
        status = TransactionStatus.REJECTED;
        return true;
    }
}