import model.Flight;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import services.CustomException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import static org.springframework.http.MediaType.APPLICATION_JSON;

public class NewFlightClient {

    RestClient restClient = RestClient.builder().requestInterceptor(new CustomRestClientInterceptor()).build();
    public static final String URL = "http://localhost:8080/flights";
    private <T> T execute(Callable<T> callable) {
        try {
            return callable.call();
        } catch (ResourceAccessException | HttpClientErrorException e) { // server down, resource exception
            throw new CustomException(e.getMessage());
        } catch (Exception e) {
            throw new CustomException(e.getMessage());
        }
    }

    public List<Flight> getAllFlights() {
        return Arrays.stream(execute(() -> restClient.get().uri(URL).retrieve().body(Flight[].class))).toList();
    }

    public Flight getFlight(String flightNumber) {
        return execute(() -> restClient.get().uri(URL + "/" + flightNumber).retrieve().body(Flight.class));
    }

    public Flight createFlight(Flight flight) {
        return execute(() -> restClient.post().uri(URL).contentType(APPLICATION_JSON).body(flight).retrieve().body(Flight.class));
    }

    public void deleteFlight(String flightNumber) {
        execute(() -> restClient.delete().uri(URL + "/" + flightNumber).retrieve().body(Flight.class));
    }

    public void updateFlight(String flightNumber, Flight flight) {
        execute(() -> restClient.put().uri(URL + "/" + flightNumber).body(flight).retrieve().body(Flight.class));
    }

    public class CustomRestClientInterceptor
            implements ClientHttpRequestInterceptor {

        @Override
        public ClientHttpResponse intercept(
                HttpRequest request,
                byte[] body,
                ClientHttpRequestExecution execution) throws IOException {
            System.out.println("Sending a "+request.getMethod()+ " request to "+request.getURI()+ " and body ["+new String(body)+"]");
            ClientHttpResponse response=null;
            try {
                response = execution.execute(request, body);
                System.out.println("Got response code " + response.getStatusCode());
            }catch(IOException ex){
                System.err.println("Eroare executie "+ex);
            }
            return response;
        }
    }
}
