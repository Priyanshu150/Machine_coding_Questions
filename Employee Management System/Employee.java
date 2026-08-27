
enum EmployeeStatus {
    ACTIVE,
    ON_LEAVE,
    RESIGNED
}

abstract class Employee{
    private final int employeeId;
    private String name;
    private String email;
    private double baseSalary;
    private EmployeeStatus status;         

    public Employee(int employeeId, String name, String email, double  baseSalary) {
        this.employeeId = employeeId;
        this.name = name;
        this.email = email;
        this.baseSalary = baseSalary;
        status = EmployeeStatus.ACTIVE;
    }

    public int getEmpoloyeeId(){
        return employeeId;
    }

    public String getName(){
        return name;
    }

    public String getEmail(){
        return email;
    }
    
    public double getBaseSalary(){
        return baseSalary;
    }

    public EmployeeStatus getEmployeeStatus(){
        return status;
    }

    public void changeStatus(EmployeeStatus status) {
        this.status = status;
    }

    public abstract double calculateSalary();
}