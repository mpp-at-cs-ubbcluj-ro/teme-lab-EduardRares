package networking.protobuffprotocol;

import model.Flight;
import model.Ticket;
import model.Employee;
import networking.dto.DestDepartureDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


public class ProtoUtils {
    public static AppProtobufs.AppRequest createLoginRequest(Employee user){
        AppProtobufs.Employee userDTO=AppProtobufs.Employee.newBuilder()
                .setUsername(user.getUsername())
                .setPassword(user.getPassword()).build();
        AppProtobufs.AppRequest request= AppProtobufs.AppRequest.newBuilder().setType(AppProtobufs.AppRequest.Type.LOGIN)
                .setUser(userDTO).build();
        return request;
    }
    public static AppProtobufs.AppRequest createLogoutRequest(Employee user){
        AppProtobufs.Employee userDTO=AppProtobufs.Employee.newBuilder().setId(user.getId()).build();
        AppProtobufs.AppRequest request= AppProtobufs.AppRequest.newBuilder().setType(AppProtobufs.AppRequest.Type.LOGOUT)
                .setUser(userDTO).build();
        return request;
    }

    public static AppProtobufs.AppRequest createAddTicketRequest(Ticket ticket){
        AppProtobufs.DateTime dateTimeDTO = AppProtobufs.DateTime.newBuilder()
                .setYear(ticket.getFlight().getDepartureTime().getYear())
                .setMonth(ticket.getFlight().getDepartureTime().getMonth().getValue())
                .setDay(ticket.getFlight().getDepartureTime().getDayOfMonth())
                .setHours(ticket.getFlight().getDepartureTime().getHour())
                .setMinutes(ticket.getFlight().getDepartureTime().getMinute())
                .build();
        AppProtobufs.Flight flightDTO=AppProtobufs.Flight.newBuilder()
                .setId(ticket.getFlight().getId())
                .setDestination(ticket.getFlight().getDestination())
                .setAirport(ticket.getFlight().getAirport())
                .setNumberOfAvailableSeats(ticket.getFlight().getNumberOfAvailableSeats())
                .setDepartureTime(dateTimeDTO)
                .build();
        AppProtobufs.Ticket ticketDTO=AppProtobufs.Ticket.newBuilder().
                setId(ticket.getId())
                .setFlight(flightDTO)
                .setNoOfTickets(ticket.getNoOfTickets())
                .addAllNames(ticket.getNames())
                .build();
        AppProtobufs.AppRequest request= AppProtobufs.AppRequest.newBuilder()
                .setType(AppProtobufs.AppRequest.Type.ADD_TICKET)
                .setTicket(ticketDTO).build();
        return request;
    }

    public static AppProtobufs.AppRequest createUpdateFlightRequest(Flight flight){
        AppProtobufs.DateTime dateTimeDTO = AppProtobufs.DateTime.newBuilder()
                .setYear(flight.getDepartureTime().getYear())
                .setMonth(flight.getDepartureTime().getMonth().getValue())
                .setDay(flight.getDepartureTime().getDayOfMonth())
                .setHours(flight.getDepartureTime().getHour())
                .setMinutes(flight.getDepartureTime().getMinute())
                .build();
        AppProtobufs.Flight flightDTO=AppProtobufs.Flight.newBuilder()
                .setId(flight.getId())
                .setDestination(flight.getDestination())
                .setAirport(flight.getAirport())
                .setNumberOfAvailableSeats(flight.getNumberOfAvailableSeats())
                .setDepartureTime(dateTimeDTO)
                .build();
        AppProtobufs.AppRequest request= AppProtobufs.AppRequest.newBuilder()
                .setType(AppProtobufs.AppRequest.Type.UPDATE_FLIGHT)
                .setFlight(flightDTO).build();
        return request;
    }

    public static AppProtobufs.AppRequest createDestDepartureRequest(String destination, LocalDateTime departureTime){
        AppProtobufs.DateTime dateTimeDTO = AppProtobufs.DateTime.newBuilder()
                .setYear(departureTime.getYear())
                .setMonth(departureTime.getMonth().getValue())
                .setDay(departureTime.getDayOfMonth())
                .build();
        AppProtobufs.AppRequest request = AppProtobufs.AppRequest.newBuilder()
                .setType(AppProtobufs.AppRequest.Type.GET_BY_DEST_DEPARTURE)
                .setDestination(destination)
                .setDeparture(dateTimeDTO)
                .build();
        return request;
    }

    public static AppProtobufs.AppRequest createGetAllFLightsRequest(){
        AppProtobufs.AppRequest request = AppProtobufs.AppRequest.newBuilder()
                .setType(AppProtobufs.AppRequest.Type.GET_ALL_FLIGHTS).build();
        return request;
    }


    public static AppProtobufs.AppResponse createOkResponse(){
        AppProtobufs.AppResponse response=AppProtobufs.AppResponse.newBuilder()
                .setType(AppProtobufs.AppResponse.Type.OK).build();
        return response;
    }

    public static AppProtobufs.AppResponse createErrorResponse(String text){
        AppProtobufs.AppResponse response=AppProtobufs.AppResponse.newBuilder()
                .setType(AppProtobufs.AppResponse.Type.ERROR)
                .setError(text).build();
        return response;
    }

    public static AppProtobufs.AppResponse createFlightUpdateResponse(){
        AppProtobufs.AppResponse response = AppProtobufs.AppResponse.newBuilder()
                .setType(AppProtobufs.AppResponse.Type.FLIGHT_UPDATED).build();
        return response;
    }

    public static AppProtobufs.AppResponse createFlightFilteredResponse(List<Flight> flights){
        List<AppProtobufs.Flight> flightsDTO = new ArrayList<>();
        for(Flight flight:flights) {
            AppProtobufs.DateTime dateTimeDTO = AppProtobufs.DateTime.newBuilder()
                    .setYear(flight.getDepartureTime().getYear())
                    .setMonth(flight.getDepartureTime().getMonth().getValue())
                    .setDay(flight.getDepartureTime().getDayOfMonth())
                    .setHours(flight.getDepartureTime().getHour())
                    .setMinutes(flight.getDepartureTime().getMinute())
                    .build();
            AppProtobufs.Flight flightDTO = AppProtobufs.Flight.newBuilder()
                    .setId(flight.getId())
                    .setDestination(flight.getDestination())
                    .setAirport(flight.getAirport())
                    .setNumberOfAvailableSeats(flight.getNumberOfAvailableSeats())
                    .setDepartureTime(dateTimeDTO)
                    .build();
            flightsDTO.add(flightDTO);
        }
        AppProtobufs.AppResponse response = AppProtobufs.AppResponse.newBuilder()
                .setType(AppProtobufs.AppResponse.Type.FLIGHT_FILTERED)
                .addAllFlights(flightsDTO).build();
        return response;
    }

    public static AppProtobufs.AppResponse createGetAllFlightsResponse(List<Flight> flights){
        List<AppProtobufs.Flight> flightsDTO = new ArrayList<>();
        for(Flight flight:flights){
            AppProtobufs.DateTime dateTimeDTO = AppProtobufs.DateTime.newBuilder()
                    .setYear(flight.getDepartureTime().getYear())
                    .setMonth(flight.getDepartureTime().getMonth().getValue())
                    .setDay(flight.getDepartureTime().getDayOfMonth())
                    .setHours(flight.getDepartureTime().getHour())
                    .setMinutes(flight.getDepartureTime().getMinute())
                    .build();
            AppProtobufs.Flight flightDTO = AppProtobufs.Flight.newBuilder()
                    .setId(flight.getId())
                    .setDestination(flight.getDestination())
                    .setAirport(flight.getAirport())
                    .setNumberOfAvailableSeats(flight.getNumberOfAvailableSeats())
                    .setDepartureTime(dateTimeDTO)
                    .build();
            flightsDTO.add(flightDTO);
        }
        AppProtobufs.AppResponse response = AppProtobufs.AppResponse.newBuilder()
                .setType(AppProtobufs.AppResponse.Type.GET_ALL_FLIGHTS)
                .addAllFlights(flightsDTO).build();
        return response;
    }

    public static String getError(AppProtobufs.AppResponse response){
        String errorMessage=response.getError();
        return errorMessage;
    }

    public static Employee getUser(AppProtobufs.AppResponse response){
        Employee user=new Employee();
        user.setId(response.getUser().getId());
        user.setPassword(response.getUser().getPassword());
        user.setUsername(response.getUser().getUsername());
        return user;
    }

    public static Employee getUser(AppProtobufs.AppRequest request){
        Employee user=new Employee();
        user.setId(request.getUser().getId());
        user.setPassword(request.getUser().getPassword());
        user.setUsername(request.getUser().getUsername());
        return user;
    }

    public static Flight getFlight(AppProtobufs.AppResponse response){
        Flight flight = new Flight();
        flight.setId(response.getFlight().getId());
        flight.setAirport(response.getFlight().getAirport());
        flight.setDestination(response.getFlight().getDestination());
        flight.setNumberOfAvailableSeats(response.getFlight().getNumberOfAvailableSeats());
        LocalDateTime ldt = LocalDateTime.of(
                response.getFlight().getDepartureTime().getYear(),
                response.getFlight().getDepartureTime().getMonth(),
                response.getFlight().getDepartureTime().getDay(),
                response.getFlight().getDepartureTime().getHours(),
                response.getFlight().getDepartureTime().getMinutes(),
                response.getFlight().getDepartureTime().getSeconds(),
                response.getFlight().getDepartureTime().getNanos()
        );
        flight.setDepartureTime(ldt);
        return flight;
    }

    public static Flight getFlight(AppProtobufs.AppRequest request){
        Flight flight = new Flight();
        flight.setId(request.getFlight().getId());
        flight.setAirport(request.getFlight().getAirport());
        flight.setDestination(request.getFlight().getDestination());
        flight.setNumberOfAvailableSeats(request.getFlight().getNumberOfAvailableSeats());
        LocalDateTime ldt = LocalDateTime.of(
                request.getFlight().getDepartureTime().getYear(),
                request.getFlight().getDepartureTime().getMonth(),
                request.getFlight().getDepartureTime().getDay(),
                request.getFlight().getDepartureTime().getHours(),
                request.getFlight().getDepartureTime().getMinutes(),
                request.getFlight().getDepartureTime().getSeconds(),
                request.getFlight().getDepartureTime().getNanos()
        );
        flight.setDepartureTime(ldt);
        return flight;
    }

    public static Ticket getTicket(AppProtobufs.AppRequest request){
        Flight flight = new Flight();
        flight.setId(request.getTicket().getFlight().getId());
        flight.setAirport(request.getTicket().getFlight().getAirport());
        flight.setDestination(request.getTicket().getFlight().getDestination());
        LocalDateTime ldt = LocalDateTime.of(
                request.getTicket().getFlight().getDepartureTime().getYear(),
                request.getTicket().getFlight().getDepartureTime().getMonth(),
                request.getTicket().getFlight().getDepartureTime().getDay(),
                request.getTicket().getFlight().getDepartureTime().getHours(),
                request.getTicket().getFlight().getDepartureTime().getMinutes(),
                request.getTicket().getFlight().getDepartureTime().getSeconds(),
                request.getTicket().getFlight().getDepartureTime().getNanos()
        );
        flight.setDepartureTime(ldt);
        Ticket ticket=new Ticket();
        ticket.setId(request.getTicket().getId());
        ticket.setNoOfTickets(request.getTicket().getNoOfTickets());
        ticket.setFlight(flight);
        ticket.setNames(request.getTicket().getNamesList());
        return ticket;
    }

    public static List<Flight> getFlights(AppProtobufs.AppRequest request){
        List<Flight> flights=new ArrayList<>();
        List<AppProtobufs.Flight> flightsDTO=request.getFlightsList();
        for(AppProtobufs.Flight flightDTO:flightsDTO){
            Flight flight = new Flight();
            flight.setId(request.getFlight().getId());
            flight.setAirport(request.getFlight().getAirport());
            flight.setDestination(request.getFlight().getDestination());
            LocalDateTime ldt = LocalDateTime.of(
                    request.getFlight().getDepartureTime().getYear(),
                    request.getFlight().getDepartureTime().getMonth(),
                    request.getFlight().getDepartureTime().getDay(),
                    request.getFlight().getDepartureTime().getHours(),
                    request.getFlight().getDepartureTime().getMinutes(),
                    request.getFlight().getDepartureTime().getSeconds(),
                    request.getFlight().getDepartureTime().getNanos()
            );
            flight.setDepartureTime(ldt);
            flights.add(flight);
        }
        return flights;
    }

    public static List<Flight> getFlights(AppProtobufs.AppResponse response){
        List<Flight> flights=new ArrayList<>();
        List<AppProtobufs.Flight> flightsDTO=response.getFlightsList();
        for(AppProtobufs.Flight flightDTO:flightsDTO){
            Flight flight = new Flight();
            flight.setId(flightDTO.getId());
            flight.setAirport(flightDTO.getAirport());
            flight.setDestination(flightDTO.getDestination());
            flight.setNumberOfAvailableSeats(flightDTO.getNumberOfAvailableSeats());
            AppProtobufs.DateTime dtoDeparture = flightDTO.getDepartureTime();
            LocalDateTime ldt = LocalDateTime.of(
                    dtoDeparture.getYear(),
                    dtoDeparture.getMonth(),
                    dtoDeparture.getDay(),
                    dtoDeparture.getHours(),
                    dtoDeparture.getMinutes(),
                    dtoDeparture.getSeconds(),
                    dtoDeparture.getNanos()
            );
            flight.setDepartureTime(ldt);
            flights.add(flight);
        }
        return flights;
    }

    public static DestDepartureDTO getDestDepartureDTO(AppProtobufs.AppRequest request){
        String destination = request.getDestination();
        LocalDateTime ldt = LocalDateTime.of(
                request.getDeparture().getYear(),
                request.getDeparture().getMonth(),
                request.getDeparture().getDay(),
                request.getDeparture().getHours(),
                request.getDeparture().getMinutes(),
                request.getDeparture().getSeconds(),
                request.getDeparture().getNanos()
        );
        return new DestDepartureDTO(destination, ldt);
    }

}
