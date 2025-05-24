package networking.rpcProtocol.ams;
import model.Employee;
import model.Flight;
import model.Ticket;
import networking.dto.DestDepartureDTO;
import networking.rpcProtocol.Request;
import networking.rpcProtocol.Response;
import networking.rpcProtocol.ResponseType;
import services.CustomException;
import services.IServiceAMS;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.Optional;

public class AppClientAMSRpcReflectionWorker implements Runnable {
    private IServiceAMS server;
    private Socket connection;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;
    public AppClientAMSRpcReflectionWorker(IServiceAMS server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try{
            output=new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input=new ObjectInputStream(connection.getInputStream());
            connected=true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while(connected){
            try {
                Object request = input.readObject();
                System.out.println("Received request");
                Response response = handleRequest((Request) request);
                if (response != null) {
                    sendResponse(response);
                }
            }catch (EOFException | SocketException e) {
                System.out.println("Connection closed or reset by server.");
            } catch (IOException|ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            System.out.println("Error "+e);
        }
    }

    private static Response okResponse=new Response.Builder().type(ResponseType.OK).build();

    private Response handleRequest(Request request){
        Response response=null;
        String handlerName="handle"+(request).type();
        System.out.println("HandlerName "+handlerName);
        try {
            Method method=this.getClass().getDeclaredMethod(handlerName, Request.class);
            response=(Response)method.invoke(this,request);
            System.out.println("Method "+handlerName+ " invoked");
        } catch (NoSuchMethodException|InvocationTargetException|IllegalAccessException e) {
            e.printStackTrace();
        }
        return response;
    }

    private Response handleLOGIN(Request request){
        System.out.println("Login request ..."+request.type());
        Employee employee=(Employee) request.data();
        try {
            Optional<Employee> user = server.login(employee.getUsername(), employee.getPassword());
            return new Response.Builder().type(ResponseType.LOGIN).data(user.get()).build();
        } catch (CustomException e) {
            connected=false;
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleLOGOUT(Request request){
        System.out.println("Logout request");
        Employee employee=(Employee)request.data();
        try {
            server.logout(employee);
            connected=false;
            return okResponse;

        } catch (CustomException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleADD_EMPLOYEE(Request request){
        System.out.println("Adding ticket Request ...");
        Employee employee=(Employee) request.data();
        try {
            server.addEmployee(employee);
            return okResponse;
        } catch (CustomException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleUPDATE_FLIGHT(Request request){
        System.out.println("Update Request ...");
        Flight flight=(Flight)request.data();
        try {
            server.updateFlight(flight);
            return okResponse;
        } catch (CustomException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleGET_BY_DEST_DEPARTURE(Request request){
        System.out.println("Get by Destination and Departure Request ...");
        DestDepartureDTO dddto=(DestDepartureDTO)request.data();
        try {
            List<Flight> flights=server.getbyDestDeparture(dddto.getDestination(), dddto.getDepartureTime());
            return new Response.Builder().type(ResponseType.FLIGHT_FILTERED).data(flights).build();
        } catch (CustomException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleADD_TICKET(Request request){
        System.out.println("Adding ticket Request ...");
        Ticket ticket=(Ticket) request.data();
        try {
            server.addTicket(ticket);
            return okResponse;
        } catch (CustomException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleGET_ALL_FLIGHTS(Request request){
        System.out.println("Update Request ...");
        try {
            List<Flight> flights=server.getAllFlights();
            return new Response.Builder().type(ResponseType.GET_ALL_FLIGHTS).data(flights).build();
        } catch (CustomException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private void sendResponse(Response response) throws IOException{
        System.out.println("sending response "+response);
        output.writeObject(response);
        output.flush();
    }
}
