package server;

import model.Employee;
import model.Flight;
import model.Ticket;
import persistence.EmployeeRepoInterface;
import persistence.FlightRepositoryInterface;
import persistence.TicketRepoInterface;
import services.CustomException;
import services.IObserver;
import services.IService;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerImpl implements IService {
    private EmployeeRepoInterface employeeRepo;
    private TicketRepoInterface ticketRepo;
    private FlightRepositoryInterface flightRepo;
    private Map<Integer, IObserver> loggedClients;
    private final int defaultNoThreads = 5;

    public ServerImpl(EmployeeRepoInterface employeeRepo, TicketRepoInterface ticketRepo, FlightRepositoryInterface flightRepo) {
        this.employeeRepo = employeeRepo;
        this.ticketRepo = ticketRepo;
        this.flightRepo = flightRepo;
        loggedClients = new HashMap<>();
    }

    private void notifyClients() throws CustomException {
        System.out.println("Notify");
        ExecutorService executor= Executors.newFixedThreadPool(defaultNoThreads);
            for(Integer userId: loggedClients.keySet()){
            IObserver client=loggedClients.get(userId);
            if (client!=null)
                executor.execute(() -> {
                    try {
                        System.out.println("Notifying [" + userId+ "]");
                        client.update(flightRepo.findAll());
                    } catch (CustomException e) {
                        System.err.println("Error notifying " + e);
                    }
                });
        }
        executor.shutdown();
    }

    @Override
    public synchronized Optional<Employee> addEmployee(Employee employee) throws CustomException {
        return employeeRepo.save(employee);
    }

    @Override
    public synchronized Optional<Employee> login(String username, String password, IObserver client) throws CustomException {
        Optional<Employee> user = employeeRepo.login(username, password);
        if(user.isPresent()) {
            if(loggedClients.containsKey(user.get().getId())) {throw new CustomException("User already logged in.");}
            else {
                loggedClients.put(user.get().getId(), client);
            }
        }
        else{
            throw  new CustomException("No user");
        }
        return user;
    }

    @Override
    public void logout(Employee employee, IObserver client) throws CustomException {
        IObserver localClient=loggedClients.remove(employee.getId());
        if (localClient==null)
            throw new CustomException("User "+employee.getId()+" is not logged in.");
    }

    @Override
    public synchronized void updateFlight(Flight flight) throws CustomException {
        flightRepo.update(flight);
        notifyClients();
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
