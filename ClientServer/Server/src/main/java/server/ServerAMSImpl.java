package server;

import model.Employee;
import model.Flight;
import model.Ticket;
import persistence.EmployeeRepoInterface;
import persistence.FlightRepositoryInterface;
import persistence.TicketRepoInterface;
import services.CustomException;
import services.INotificationService;
import services.IObserver;
import services.IServiceAMS;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ServerAMSImpl implements IServiceAMS {
    private EmployeeRepoInterface employeeRepo;
    private TicketRepoInterface ticketRepo;
    private FlightRepositoryInterface flightRepo;
    private Map<Integer, Employee> loggedClients;
    private INotificationService notificationService;

    public ServerAMSImpl(EmployeeRepoInterface employeeRepo, TicketRepoInterface ticketRepo, FlightRepositoryInterface flightRepo, INotificationService notificationService) {
        this.employeeRepo = employeeRepo;
        this.ticketRepo = ticketRepo;
        this.flightRepo = flightRepo;
        this.notificationService = notificationService;
        this.loggedClients = new HashMap<>();
    }

    @Override
    public Optional<Employee> addEmployee(Employee employee) throws CustomException {
        return employeeRepo.save(employee);
    }

    @Override
    public synchronized Optional<Employee> login(String username, String password) throws CustomException {
        Optional<Employee> user = employeeRepo.login(username, password);
        if(user.isPresent()) {
            if(loggedClients.containsKey(user.get().getId())) {throw new CustomException("User already logged in.");}
            else {
                loggedClients.put(user.get().getId(), user.get());
            }
        }
        else{
            throw  new CustomException("No user");
        }
        return user;
    }

    @Override
    public void logout(Employee employee) throws CustomException {
        Employee localClient=loggedClients.remove(employee.getId());
        if (localClient==null)
            throw new CustomException("User "+employee.getId()+" is not logged in.");
    }

    @Override
    public synchronized void updateFlight(Flight flight) throws CustomException {
        flightRepo.update(flight);
        notificationService.updateFlight(flight);
    }

    @Override
    public synchronized List<Flight> getbyDestDeparture(String destination, LocalDateTime departureTime) throws CustomException {
        return flightRepo.findbyDestDeparture(destination, departureTime);
    }

    @Override
    public synchronized List<Flight> getAllFlights() throws CustomException {
        return flightRepo.findAll();
    }

    @Override
    public synchronized void addTicket(Ticket ticket) throws CustomException {
        ticketRepo.save(ticket);
    }
}
