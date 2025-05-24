package client;

import model.Flight;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class FlightsModel extends AbstractListModel {
    private List<Flight> flights;

    public FlightsModel() {
        flights = new ArrayList<Flight>();
    }

    @Override
    public int getSize() {
        return flights.size();
    }

    @Override
    public Object getElementAt(int index) {
        return flights.get(index);
    }

    public void addFlights(List<Flight> flight) {
        flights.addAll(flight);
    }

    public void flightUpdated(String flightId, Integer seats) {
        for(Flight flight : flights) {
            if(flight.getId().equals(flightId)) {
                flight.setNumberOfAvailableSeats(seats);
            }
        }
    }
}
