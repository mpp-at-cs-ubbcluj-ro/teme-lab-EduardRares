package persistence;

import model.Employee;

import java.util.Optional;

public interface EmployeeRepoInterface extends Repository<Integer, Employee> {
    public Optional<Employee> login(String username, String password);
}
