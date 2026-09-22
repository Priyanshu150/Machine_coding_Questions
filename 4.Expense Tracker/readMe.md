# 💰 Expense Tracker

> **Level 2 — Collections + Java Features**

An in-memory corporate Expense Tracker built using core Java, demonstrating **Functional Interfaces** (`Predicate`, `Function`, `Consumer`, `Supplier`), **Stream API**, and clean separation between data storage and processing logic.

---

## 📌 Table of Contents

- [Problem Overview](#problem-overview)
- [Class Design](#class-design)
  - [Enums](#enums)
  - [Employee](#employee)
  - [ExpenseTransaction](#expensetransaction)
  - [TransactionRepository](#transactionrepository)
  - [TransactionProcessor](#transactionprocessor)
- [Functional Interface Usage](#functional-interface-usage)
- [Key Design Notes](#key-design-notes)
- [Learnings](#-learnings)
- [Interview Q&A](#-interview-qa)

---

## Problem Overview

Design and implement a **corporate Expense Tracker** that allows employees to submit expense transactions, and managers or finance teams to approve or reject them. The system separates **data management** (`TransactionRepository`) from **data processing** (`TransactionProcessor`), with processing logic built entirely around Java's built-in functional interfaces.

---

## Class Design

### Enums

```java
enum Department {
    ENGINEERING,
    HR,
    FINANCE,
    SALES,
    MARKETING
}

enum ExpenseCategory {
    TRAVEL,
    FOOD,
    HOTEL,
    EQUIPMENT,
    TRAINING
}

enum TransactionStatus {
    PENDING,
    APPROVED,
    REJECTED
}
```

---

### `Employee`

Represents the employee who submits an expense. Identity fields are `final` — an employee's ID and name should not change.

```java
public class Employee {
    private final String empId;
    private final String empName;
    private Department department;      // mutable — employee can change department
}
```

---

### `ExpenseTransaction`

The core data model representing a single expense submission. Most fields are `final` — the facts of a transaction don't change after submission. Only `status` is mutable as it progresses through approval.

```java
public class ExpenseTransaction {
    private final String transactionId;
    private final Employee employee;
    private final Department department;
    private final double amount;
    private final ExpenseCategory expenseCategory;
    private TransactionStatus status;   // mutable — PENDING → APPROVED / REJECTED
}
```

---

### `TransactionRepository`

Responsible **only** for storing and retrieving transactions. No processing logic lives here.

```java
class TransactionRepository {
    private final Map<String, ExpenseTransaction> store;   // transactionId → ExpenseTransaction
}
```

| Method | Return Type | Description |
|---|---|---|
| `add(ExpenseTransaction t)` | `boolean` | Add a new expense transaction |
| `remove(String transactionId)` | `boolean` | Remove a transaction by ID |
| `find(String transactionId)` | `Optional<ExpenseTransaction>` | Find a transaction by ID |
| `getAll()` | `Collection<ExpenseTransaction>` | Return all stored transactions |

---

### `TransactionProcessor`

Responsible **only** for processing, filtering, aggregating, and analysing transactions. Accepts a `TransactionRepository` as a dependency.

```java
class TransactionProcessor {
    private final TransactionRepository repository;
}
```

#### Functional Operations

| Method | Functional Interface | Description |
|---|---|---|
| `filter(Predicate<ExpenseTransaction>)` | `Predicate<T>` | Return transactions matching a custom condition |
| `transform(Function<ExpenseTransaction, R>)` | `Function<T, R>` | Map each transaction to another type/value |
| `processEach(Consumer<ExpenseTransaction>)` | `Consumer<T>` | Perform an action on each transaction (e.g. print, log) |
| `createDefault(Supplier<ExpenseTransaction>)` | `Supplier<T>` | Create a default transaction when none is found |

#### Business Queries

| Method | Return Type | Description |
|---|---|---|
| `getApprovedTransactions()` | `List<ExpenseTransaction>` | Return all transactions with status `APPROVED` |
| `getPendingTransactions()` | `List<ExpenseTransaction>` | Return all transactions with status `PENDING` |
| `getTotalApprovedAmount()` | `double` | Sum of all approved transaction amounts |
| `getHighestExpense()` | `Optional<ExpenseTransaction>` | Return the single highest-value transaction |
| `groupByDepartment()` | `Map<Department, List<ExpenseTransaction>>` | Group all transactions by department |
| `groupByCategory()` | `Map<ExpenseCategory, List<ExpenseTransaction>>` | Group all transactions by expense category |
| `groupByEmployee()` | `Map<Employee, List<ExpenseTransaction>>` | Group all transactions by employee |
| `sortByAmount()` | `List<ExpenseTransaction>` | Return transactions sorted by amount (ascending) |
| `getTransactionsByDepartment(Department)` | `List<ExpenseTransaction>` | Filter transactions for a specific department |
| `getTransactionsByEmployee(String empId)` | `List<ExpenseTransaction>` | Filter transactions for a specific employee |

---

## Functional Interface Usage

Each of the four core Java functional interfaces maps to a real operation in `TransactionProcessor`:

### `Predicate<ExpenseTransaction>` — filter
```java
// Get all approved travel expenses above ₹5000
List<ExpenseTransaction> result = processor.filter(
    t -> t.getStatus() == TransactionStatus.APPROVED
      && t.getExpenseCategory() == ExpenseCategory.TRAVEL
      && t.getAmount() > 5000
);
```

### `Function<ExpenseTransaction, R>` — transform
```java
// Extract just the employee names from all transactions
List<String> employeeNames = processor.transform(
    t -> t.getEmployee().getEmpName()
);
```

### `Consumer<ExpenseTransaction>` — processEach
```java
// Print each pending transaction for review
processor.processEach(t ->
    System.out.println(t.getTransactionId() + " | "
        + t.getEmployee().getEmpName() + " | ₹"
        + t.getAmount())
);
```

### `Supplier<ExpenseTransaction>` — createDefault
```java
// Return a default fallback transaction if none is found
ExpenseTransaction fallback = processor.createDefault(
    () -> new ExpenseTransaction("DEFAULT", defaultEmployee,
              Department.ENGINEERING, 0.0,
              ExpenseCategory.OTHER, TransactionStatus.PENDING)
);
```

---

## Key Design Notes

**1. Repository vs Processor — Separation of Concerns**

| Class | Responsibility |
|---|---|
| `TransactionRepository` | CRUD operations — add, remove, find, list |
| `TransactionProcessor` | Analysis — filter, aggregate, group, sort |

This separation means storage can be swapped (e.g. to a database) without touching processing logic, and vice versa.

**2. Functional Interfaces Enable Flexible Queries**

Rather than writing a new method for every possible filter condition, `filter(Predicate<ExpenseTransaction>)` lets the caller pass in any condition. This keeps `TransactionProcessor` lean while supporting unlimited query combinations.

**3. `Map<String, ExpenseTransaction>` over `List`**

Using `transactionId` as the key gives O(1) lookup for `find` and `remove`, versus O(n) with a `List`.

**4. `ExpenseTransaction.status` is the Only Mutable Field**

All other fields (`amount`, `employee`, `department`, `category`) are `final` — a transaction's submitted facts don't change. Status (`PENDING → APPROVED / REJECTED`) is the one legitimate state transition allowed post-submission.

**5. Grouping with `Collectors.groupingBy`**

```java
// Group by department
Map<Department, List<ExpenseTransaction>> byDept = repository.getAll().stream()
    .collect(Collectors.groupingBy(ExpenseTransaction::getDepartment));

// Group by category
Map<ExpenseCategory, List<ExpenseTransaction>> byCat = repository.getAll().stream()
    .collect(Collectors.groupingBy(ExpenseTransaction::getExpenseCategory));
```

**6. `department` on Both `Employee` and `ExpenseTransaction`**

`Employee.department` represents where the employee currently belongs. `ExpenseTransaction.department` captures the department at the time of submission — these can differ if an employee is transferred, and keeping them separate ensures historical accuracy.

---

---

## 📖 Learnings

### Functional Interfaces in `java.util.function`

Functional interfaces allow **behaviour to be passed as a parameter**, making code flexible and reusable without writing separate methods for every condition or action.

| Interface | Input | Output | Purpose |
|---|---|---|---|
| `Predicate<T>` | `T` | `boolean` | Represents a condition — use for filtering |
| `Function<T, R>` | `T` | `R` | Transforms a value from one type to another |
| `Consumer<T>` | `T` | `void` | Performs an action without returning a value |
| `Supplier<T>` | none | `T` | Produces a value without taking any input |
| `Comparator<T>` | `T, T` | `int` | Defines ordering between two objects |

```
Predicate  → filter(t -> t.getAmount() > 5000)
Function   → transform(t -> t.getEmployee().getEmpName())
Consumer   → processEach(t -> System.out.println(t))
Supplier   → createDefault(() -> new ExpenseTransaction(...))
Comparator → sortByAmount using Comparator.comparingDouble(...)
```

---

## ❓ Interview Q&A

### Q1. Why have `filter(Predicate)` if you already have `getApprovedTransactions()`?

**Answer:**

`getApprovedTransactions()` is a **specific business operation** — it encodes one fixed rule (status == APPROVED) and is named for readability and intent.

`filter(Predicate<ExpenseTransaction>)` is a **generic, reusable operation** — it lets the caller pass in *any* condition without requiring a new method for every possible combination.

```java
// Specific — readable, intent is clear
processor.getApprovedTransactions();

// Generic — flexible, handles any condition the caller needs
processor.filter(t -> t.getStatus() == TransactionStatus.APPROVED
                   && t.getDepartment() == Department.ENGINEERING
                   && t.getAmount() > 10000);
```

Both have a place in the design:
- Use **named methods** for common, well-understood business rules that appear frequently
- Use **`filter(Predicate)`** for ad-hoc, one-off, or composite queries

> This is the same principle behind Java's `Stream.filter()` — the stream API doesn't have `filterByStatus()` or `filterByAmount()`; it has one flexible `filter(Predicate)` that covers everything.

---

> 💡 **Next Step:** Implement the classes above and test using a `Main.java` runner that creates employees across departments, submits expense transactions in various categories, approves/rejects them, and runs grouping and aggregation queries.