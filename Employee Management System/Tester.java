
class Tester extends Employee{
    public Tester(int employeeId, String name, String email, double  baseSalary) {
        super(employeeId, name, email, baseSalary);
    }

    @Override
    public double calculateSalary() {
        return getBaseSalary() + (0.10 * getBaseSalary());   // Base + 20% bonus
    }
}