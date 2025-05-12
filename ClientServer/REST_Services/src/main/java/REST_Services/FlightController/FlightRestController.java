package REST_Services.FlightController;

import model.Flight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import persistence.FlightRepositoryInterface;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:55556")
@RestController
@RequestMapping("/flights")
public class FlightRestController {
    @Autowired
    private FlightRepositoryInterface flightRepository;

    @PostMapping
    public Optional<Flight> addFlight(@RequestBody Flight flight) {
        return flightRepository.save(flight);
    }

    @GetMapping
    public List<Flight> getAllFlights() {
       return flightRepository.findAll();
    }

    @GetMapping("/filter")
    public List<Flight> getFilteredFlights(@RequestParam String destination,  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)  LocalDateTime dateTime) {
        return flightRepository.findbyDestDeparture(destination, dateTime);
    }

    @GetMapping("/{id}")
    public Optional<Flight> getFlightById(@PathVariable String id) {
        return flightRepository.findOne(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Optional<Flight> updateFlight(@PathVariable String id, @RequestBody Flight flight) {
        return flightRepository.update(flight);
    }

    @RequestMapping(value="/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteFlight(@PathVariable String id) {
        Optional<Flight> flight = flightRepository.delete(id);
        if (flight.isPresent()) {
            return new ResponseEntity<>(flight.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


}
