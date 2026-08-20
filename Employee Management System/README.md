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