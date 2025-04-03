package persistence;

import model.Flight;
import model.Ticket;

public interface TicketRepoInterface extends Repository<Integer, Ticket>{
    public Flight findOneFlight(String s);
}
