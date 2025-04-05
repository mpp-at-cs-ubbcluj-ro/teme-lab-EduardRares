package model;

import java.io.Serializable;
import java.util.List;

public class Ticket implements Entity<Integer>, Comparable<Ticket>, Serializable {
    private List<String> names;
    private int noOfTickets;
    private Flight flight;
    private Integer id;

    public Ticket(List<String> names, int noOfTickets, Flight flight) {
        this.names = names;
        this.noOfTickets = noOfTickets;
        this.flight = flight;
    }

    public Ticket() {}

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

    @Override
    public int compareTo(Ticket o) {
        return id.compareTo(o.id);
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer integer) {
        id = integer;
    }
}
