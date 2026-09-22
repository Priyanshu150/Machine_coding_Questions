import java.util.*;

class EmployeeManager{
    private final Map<Integer, Employee> empMap;

    public EmployeeManager() {
        this.empMap = new HashMap<>();
    }

    public boolean addEmployee(Employee employee){
        // get employee id 
        int empId = employee.getEmpoloyeeId();

        // check if emplyee already added 
        if(empMap.containsKey(empId)){
            return false;
        }

        // add employee 
        empMap.put(empId, employee);
        // employee added 
        return true;
    }

    public boolean removeEmployee(int empId){
        // check if employee present or not 
        if(!empMap.containsKey(empId)){
            return false;
        }
        // remove employee from the map 
        empMap.remove(empId);
        // employee removed 
        return true;
    }

    public Optional<Employee> findEmployee(int empId){
        // find the employee in map, 
        // not present then return null
        // otherwise return the object 
        return Optional.ofNullable(empMap.get(empId));
    }

    public double calculatePayroll(){
        return empMap.values()
                    .stream()
                    .mapToDouble(Employee::calculateSalary)
                    .sum();
    }

    public Optional<Employee> highestPaidEmployee(){
        if(empMap.isEmpty())
            return Optional.empty();
        
        // max() on an empty stream already returns Optional.empty()
        return empMap.values()
                    .stream()
                    .max(Comparator.comparingDouble(Employee::calculateSalary));
    
    }

    public List<Employee> getEmployeeByType(Class<? extends Employee> type){
        return empMap.values()
                    .stream()
                    .filter(employee -> employee.getClass() == type)
                    .toList();
    }
    // way to call getEmployeeByType function 
    // List<Employee> developers =
    //     employeeManager.getEmployeesByType(Developer.class);

    public List<Employee> getEmployeeBySalary(){
        // sort in descending order
        return empMap.values()
                .stream()
                .sorted(Comparator.comparingDouble(Employee::calculateSalary).reversed())
                .toList();

        
        // sort in ascending order 
        /*
            return empMap.values()
                .stream()
                .sorted(Comparator.comparingDouble(Employee::calculateSalary))
                .toList();
        */
    }

    public List<Employee> getActiveEmployee(){
        return empMap.values()
                .stream()
                .filter(e -> e.getEmployeeStatus() == EmployeeStatus.ACTIVE)
                .toList();
    }

    public List<Employee> getEmployeesOnLeave(){
        return empMap.values()
                .stream()
                .filter(e -> e.getEmployeeStatus() == EmployeeStatus.ON_LEAVE)
                .toList();
    }
}