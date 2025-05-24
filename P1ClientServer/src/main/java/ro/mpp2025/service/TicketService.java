package ro.mpp2025.service;

import ro.mpp2025.Repository.TicketRepoInterface;
import ro.mpp2025.model.Ticket;

public class TicketService {
    private TicketRepoInterface ticketRepo;
    public TicketService(TicketRepoInterface ticketRepo) {
        this.ticketRepo = ticketRepo;
    }
    public void add(Ticket ticket) {
        ticketRepo.save(ticket);
    }
}
