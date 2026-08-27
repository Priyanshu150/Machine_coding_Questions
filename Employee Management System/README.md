# 🧑‍💼 Employee Management System

> **Level 1 — OOP Foundation**

An in-memory Employee Management System built using core Java, demonstrating **Inheritance**, **Abstraction**, and **Polymorphism** through a hierarchy of employee types.

---

## 📌 Table of Contents

- [Problem Overview](#problem-overview)
- [Class Design](#class-design)
  - [Abstract Class: Employee](#abstract-class-employee)
  - [Concrete Classes](#concrete-classes)
  - [EmployeeManagement](#employeemanagement)
- [Salary Calculation Rules](#salary-calculation-rules)
- [Key Design Notes](#key-design-notes)

---

## Problem Overview

Design and implement an **Employee Management System** that supports multiple employee types, each with their own salary calculation logic. The system should allow adding, removing, and querying employees, as well as payroll and reporting operations.

---

## Class Design

### Abstract Class: `Employee`

The base class for all employee types. Enforces a salary contract via an abstract method.

```java
abstract class Employee {
    int employeeId;
    String name;
    String email;
    double baseSalary;
    Status status;          // ENUM: ACTIVE, ON_LEAVE, TERMINATED

    abstract double calculateSalary();
}
```

---

### Concrete Classes

Each subclass overrides `calculateSalary()` with its own bonus logic.

#### `Developer extends Employee`

```java
@Override
double calculateSalary() {
    return baseSalary + (0.20 * baseSalary);   // Base + 20% bonus
}
```

#### `Tester extends Employee`

```java
@Override
double calculateSalary() {
    return baseSalary + (0.10 * baseSalary);   // Base + 10% bonus
}
```

#### `Manager extends Employee`

```java
@Override
double calculateSalary() {
    return baseSalary + (0.30 * baseSalary);   // Base + 30% bonus
}
```

---

### `EmployeeManagement`

The central class that manages all employees and exposes operations on them.

```java
class EmployeeManagement {
    Map<Integer, Employee> empMap;
}
```

| Method | Return Type | Description |
|---|---|---|
| `addEmployee(Employee employee)` | `boolean` | Add a new employee to the system |
| `removeEmployee(int employeeId)` | `boolean` | Remove an employee by ID |
| `findEmployee(int employeeId)` | `Optional<Employee>` | Find and return an employee by ID |
| `calculatePayroll()` | `double` | Sum of `calculateSalary()` across all employees |
| `getHighestPaidEmployee()` | `Optional<Employee>` | Return the employee with the highest calculated salary |
| `getEmployeesByType(Type type)` | `List<Employee>` | Filter employees by type (Developer / Tester / Manager) |
| `getEmployeesBySalary()` | `List<Employee>` | Return all employees sorted by salary (ascending) |
| `getActiveEmployees()` | `List<Employee>` | Return employees with status `ACTIVE` |
| `getEmployeesOnLeave()` | `List<Employee>` | Return employees with status `ON_LEAVE` |

---

## Salary Calculation Rules

| Employee Type | Bonus | Effective Salary |
|---|---|---|
| `Developer` | 20% of base | `baseSalary * 1.20` |
| `Tester` | 10% of base | `baseSalary * 1.10` |
| `Manager` | 30% of base | `baseSalary * 1.30` |

---

## Key Design Notes

**1. Use `double`, `BigInteger`, or `BigDecimal` for salary**

Prefer `BigDecimal` for financial calculations to avoid floating-point precision issues:
```java
BigDecimal salary = baseSalary.multiply(BigDecimal.valueOf(1.20));
```

**2. Salary via `abstract double calculateSalary()`**

Polymorphism ensures each subclass provides its own salary logic. The `EmployeeManagement` class never needs to check the employee type — it just calls `calculateSalary()`.

```java
employees.values().forEach(e -> total += e.calculateSalary());
```

**3. Use `Optional` for single-result queries**

Avoids `null` returns and forces the caller to handle the missing case explicitly:
```java
Optional<Employee> highestPaid = empMap.values().stream()
    .max(Comparator.comparing(Employee::calculateSalary));
```

**4. Sorting with `Comparator.comparing`**

Leverages the method reference to `calculateSalary()` for clean, readable sorting:
```java
List<Employee> sorted = empMap.values().stream()
    .sorted(Comparator.comparing(Employee::calculateSalary))
    .collect(Collectors.toList());
```

**5. `Status` as an Enum**

Keeps status values type-safe and avoids magic strings:
```java
enum Status {
    ACTIVE,
    ON_LEAVE,
    TERMINATED
}
```

---

> 💡 **Next Step:** Implement the classes above and test using a `Main.java` runner that adds employees of each type, borrows payroll totals, filters by type and status, and retrieves the highest-paid employee.

---

## ❓ Interview Q&A

### Q1. Why is this an example of runtime polymorphism?

```java
Employee employee = new Developer(...);
employee.calculateSalary();
```

**Answer:**

There are two distinct types involved here:

```
Reference type       Runtime object
     ↓                    ↓
  Employee              Developer
```

The compiler only sees the reference type `Employee` and confirms that `calculateSalary()` exists on it. But at runtime, the JVM sees that the actual object is a `Developer` and invokes `Developer.calculateSalary()`.

This is called **dynamic method dispatch** — the method to execute is resolved at runtime based on the object's actual type, not the declared reference type.

> **Interview-quality answer:** Runtime polymorphism occurs when a superclass reference refers to a subclass object and an overridden method is resolved based on the object's **actual runtime type** rather than the reference type.

---

### Q2. What is the difference between `employee.getClass() == Developer.class` and `employee instanceof Developer`?

```java
Employee employee = new Developer(...);
```

| | `employee.getClass() == Developer.class` | `employee instanceof Developer` |
|---|---|---|
| **Checks** | Exact runtime class | Compatible type in inheritance hierarchy |
| **SeniorDeveloper?** | `false` — not the exact class | `true` — is a subtype of Developer |
| **Use case** | When you need an exact type match | When any subtype is acceptable |

**Example:**

```java
class SeniorDeveloper extends Developer { }

Employee employee = new SeniorDeveloper(...);

employee.getClass() == Developer.class   // false — exact class is SeniorDeveloper
employee instanceof Developer            // true  — SeniorDeveloper IS-A Developer
```

> **Rule of thumb:** Prefer `instanceof` when working with inheritance hierarchies. Use `getClass()` only when exact type equality matters.

---

### Q3. Why is `Comparator.comparingDouble(Employee::calculateSalary)` better than writing a manual sorting algorithm?

**Answer:**

- `Comparator.comparingDouble()` leverages Java's built-in **TimSort** (O(n log n)), which is well-tested and optimised
- It keeps the code **readable and declarative** — the intent is clear at a glance
- The comparator can be easily **composed or reversed** (`.reversed()`, `.thenComparing()`) without rewriting logic
- A custom sorting algorithm would only be justified if there were complex, multi-condition rules that the built-in comparator couldn't express cleanly

---

### Q4. What is the time complexity of `calculatePayroll()` and `getEmployeesBySalary()`?

| Method | Time Complexity | Reason |
|---|---|---|
| `calculatePayroll()` | **O(n)** | Iterates over all `n` employees; each `calculateSalary()` call is O(1) |
| `getEmployeesBySalary()` | **O(n log n)** | Iterates O(n) to build the stream, then sorts O(n log n); other operations are O(1) |

---

### Q5. If we add a new `Architect` class tomorrow, what existing code needs to change?

**Answer:** Nothing needs to change in any existing class. Just create the new subclass:

```java
class Architect extends Employee {
    @Override
    double calculateSalary() {
        return baseSalary + (0.25 * baseSalary);   // Base + 25% bonus
    }
}
```

All existing methods in `EmployeeManagement` — `calculatePayroll()`, `getHighestPaidEmployee()`, `getEmployeesBySalary()` — will work automatically because they call `calculateSalary()` polymorphically.

This is the **Open/Closed Principle** in action: the system is **open for extension** (new employee types) but **closed for modification** (existing code untouched).