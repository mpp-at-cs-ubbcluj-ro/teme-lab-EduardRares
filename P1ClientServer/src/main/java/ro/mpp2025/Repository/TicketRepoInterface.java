package ro.mpp2025.Repository;

import ro.mpp2025.model.Flight;
import ro.mpp2025.model.Ticket;

public interface TicketRepoInterface extends Repository<Integer, Ticket>{
    public Flight findOneFlight(String s);
}
