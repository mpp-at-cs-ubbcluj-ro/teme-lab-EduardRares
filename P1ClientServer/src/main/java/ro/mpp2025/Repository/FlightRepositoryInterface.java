package ro.mpp2025.Repository;

import ro.mpp2025.model.Flight;

import java.time.LocalDateTime;
import java.util.List;

public interface FlightRepositoryInterface extends Repository<String, Flight>{

    public List<Flight> findbyDestDeparture(String destination, LocalDateTime departureTime);
}
