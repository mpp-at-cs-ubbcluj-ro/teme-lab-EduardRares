package ro.mpp2025.service;

import ro.mpp2025.Repository.FlightRepositoryInterface;
import ro.mpp2025.model.Flight;
import ro.mpp2025.utils.events.Event;
import ro.mpp2025.utils.events.FlightEvent;
import ro.mpp2025.utils.events.FlightEventType;
import ro.mpp2025.utils.observer.Observable;
import ro.mpp2025.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FlightService implements Observable<Event> {
    private FlightRepositoryInterface flightRepo;
    public FlightService(FlightRepositoryInterface flightRepo) {
        this.flightRepo = flightRepo;
    }

    private List<Observer<Event>> observers = new ArrayList<Observer<Event>>();
    @Override
    public void addObserver(Observer<Event> observer) {
        observers.add(observer);
    }

    public void updateFlight(Flight flight) {
        flightRepo.update(flight);
        notifyObservers(new FlightEvent(FlightEventType.UPDATE, flight));
    }

    public List<Flight> getbyDestDeparture(String destination, LocalDateTime departureTime) {
        return flightRepo.findbyDestDeparture(destination, departureTime);
    }

    public List<Flight> getAllFlights() {
        return flightRepo.findAll();
    }

    @Override
    public void removeObserver(Observer<Event> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Event t) {
        observers.stream().forEach(observer -> observer.update(t));
    }
}
