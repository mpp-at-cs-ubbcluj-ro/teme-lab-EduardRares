package ro.mpp2025.model;

import java.util.List;

public class Ticket extends Entity<Integer> {
    private Integer ID;
    private List<String> names;
    private int noOfTickets;
    private Flight flight;

    public Ticket(Integer ID, List<String> names, int noOfTickets, Flight flight) {
        this.ID = ID;
        this.names = names;
        this.noOfTickets = noOfTickets;
        this.flight = flight;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public int getNoOfTickets() {
        return noOfTickets;
    }

    public void setNoOfTickets(int noOfTickets) {
        this.noOfTickets = noOfTickets;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }
}
