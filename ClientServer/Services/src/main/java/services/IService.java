package services;

import model.Employee;
import model.Flight;
import model.Ticket;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IService {
    public Optional<Employee> addEmployee(Employee employee) throws CustomException;
    public Optional<Employee> login(String username, String password, IObserver client) throws CustomException;
    public void logout(Employee employee, IObserver client) throws CustomException;
    public void updateFlight(Flight flight) throws CustomException;
    public List<Flight> getbyDestDeparture(String destination, LocalDateTime departureTime) throws CustomException;
    public List<Flight> getAllFlights() throws CustomException;
    public void addTicket(Ticket ticket) throws CustomException;
}
