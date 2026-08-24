
class Manager extends Employee{
    public Manager(int employeeId, String name, String email, double  baseSalary) {
        super(employeeId, name, email, baseSalary);
    }

    @Override
    public double calculateSalary() {
        return getBaseSalary() + (0.30 * getBaseSalary());   // Base + 20% bonus
    }
}