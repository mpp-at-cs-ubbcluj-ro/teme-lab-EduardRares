package ro.mpp2025.Repository;

import ro.mpp2025.model.Employee;

import java.util.Optional;

public interface EmployeeRepoInterface extends Repository<Integer, Employee> {
    public Optional<Employee> login(String username, String password);
}
