import model.Flight;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClientException;
import services.CustomException;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestRestClient {
    @Test
    @DisplayName("First Test")
    public void firstTest() {
        NewFlightClient newFlightClient = new NewFlightClient();
        Flight flightT = new Flight("Barcelona", 200000, LocalTime.of(10, 0), "BarcAirport", 200);
        flightT.setId("BRC001");
        try {
            int noOfFlights = newFlightClient.getAllFlights().size();
            System.out.println("Adding a new user "+flightT);
            show(()-> System.out.println(newFlightClient.createFlight(flightT)));
            System.out.println("\nPrinting all users ...");
            show(()->{
                List<Flight> res= newFlightClient.getAllFlights();
                for(Flight f:res){
                    System.out.println(f.getId()+": "+f.getDestination());
                }
                assertEquals(noOfFlights + 1, res.size());
            });
            Flight flightT2 = newFlightClient.getFlight(flightT.getId());
            assertEquals(flightT.getId(), flightT2.getId());
            newFlightClient.deleteFlight(flightT.getId());
            assertEquals(noOfFlights, newFlightClient.getAllFlights().size());

        }catch(RestClientException ex){
            System.out.println("Exception ... "+ex.getMessage());
        }
    }

    private static void show(Runnable task) {
        try {
            task.run();
        } catch (CustomException e) {
            //  LOG.error("Service exception", e);
            System.out.println("Service exception"+ e);
        }
    }
}
