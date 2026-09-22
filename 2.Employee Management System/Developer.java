
class Developer extends Employee{
    public Developer(int employeeId, String name, String email, double  baseSalary) {
        super(employeeId, name, email, baseSalary);
    }

    @Override
    public double calculateSalary() {
        return getBaseSalary() + (0.20 * getBaseSalary());   // Base + 20% bonus
    }
}