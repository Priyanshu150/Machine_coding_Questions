import java.util.*;

class Employee {
    private final int  id;
    private final String name;
    private final Department department;

    public Employee(int id, String name, Department department) {
        this.id = id;
        this.name = name;
        // Validates immediately. If department is null, the program crashes here.
        this.department = Objects.requireNonNull(department);
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public Department getDepartment() {
        return department;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id= " + id +
                ", name='" + name + '\'' +
                ", department=" + department +
                '}';
    }
}