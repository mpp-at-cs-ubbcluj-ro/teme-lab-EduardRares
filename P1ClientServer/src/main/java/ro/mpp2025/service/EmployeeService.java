package ro.mpp2025.service;

import ro.mpp2025.Repository.EmployeeRepoInterface;
import ro.mpp2025.model.Employee;

import java.util.Optional;

public class EmployeeService {
    private EmployeeRepoInterface employeeRepo;
    public EmployeeService(EmployeeRepoInterface employeeRepo) {
        this.employeeRepo = employeeRepo;
    }

    public Optional<Employee> login(String username, String password) {
        return employeeRepo.login(username, password);
    }

    public Optional<Employee> add(Employee employee) {
        return employeeRepo.save(employee);
    }
}
