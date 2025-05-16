package ro.iss2025.repository;

import ro.iss2025.model.ComputerRepairedForm;

import java.util.List;

public interface ComputerRepairedFormRepository extends Repository<Integer, ComputerRepairedForm> {
    List<ComputerRepairedForm> filterByEmployee(String employee);
    List<ComputerRepairedForm> filterByEmployeeAndDate(String employee, String date);
}
