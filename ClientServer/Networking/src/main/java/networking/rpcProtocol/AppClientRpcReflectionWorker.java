package networking.rpcProtocol;

import model.Employee;
import model.Flight;
import model.Ticket;
import networking.dto.DestDepartureDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.CustomException;
import services.IObserver;
import services.IService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.List;
import java.util.Optional;


public class AppClientRpcReflectionWorker implements Runnable, IObserver {
    private IService server;
    private Socket connection;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;

    private static Logger logger = LogManager.getLogger(AppClientRpcReflectionWorker.class);

    public AppClientRpcReflectionWorker(IService server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try{
            output=new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input=new ObjectInputStream(connection.getInputStream());
            connected=true;
        } catch (IOException e) {
            logger.error(e);
            logger.error(e.getStackTrace());
        }
    }

    public void run() {
        while(connected){
            try {
                Object request=input.readObject();
                Response response=handleRequest((Request)request);
                if (response!=null){
                    sendResponse(response);
                }
            } catch (IOException|ClassNotFoundException e) {
                logger.error(e);
                logger.error(e.getStackTrace());
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error(e);
                logger.error(e.getStackTrace());
            }
        }
        try {
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            logger.error("Error "+e);
        }
    }


    private static Response okResponse=new Response.Builder().type(ResponseType.OK).build();

    private Response handleRequest(Request request){
        Response response=null;
        String handlerName="handle"+(request).type();
        logger.debug("HandlerName "+handlerName);
        try {
            Method method=this.getClass().getDeclaredMethod(handlerName, Request.class);
            response=(Response)method.invoke(this,request);
            logger.debug("Method "+handlerName+ " invoked");
        } catch (NoSuchMethodException|InvocationTargetException|IllegalAccessException e) {
            logger.error(e);
            logger.error(e.getStackTrace());
        }

        return response;
    }

    private Response handleLOGIN(Request request){
        logger.debug("Login request ..."+request.type());
        Employee employee=(Employee) request.data();
        try {
            Optional<Employee> user = server.login(employee.getUsername(), employee.getPassword(), this);
            return new Response.Builder().type(ResponseType.LOGIN).data(user.get()).build();
        } catch (CustomException e) {
            connected=false;
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleLOGOUT(Request request){
        logger.debug("Logout request");
        Employee employee=(Employee)request.data();
        try {
            server.logout(employee, this);
            connected=false;
            return okResponse;

        } catch (CustomException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleADD_EMPLOYEE(Request request){
        logger.debug("Adding ticket Request ...");
        Employee employee=(Employee) request.data();
        try {
            server.addEmployee(employee);
            return okResponse;
        } catch (CustomException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleUPDATE_FLIGHT(Request request){
        logger.debug("Update Request ...");
        Flight flight=(Flight)request.data();
        try {
            server.updateFlight(flight);
            return okResponse;
        } catch (CustomException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleGET_BY_DEST_DEPARTURE(Request request){
        logger.debug("Get by Destination and Departure Request ...");
        DestDepartureDTO dddto=(DestDepartureDTO)request.data();
        try {
            List<Flight> flights=server.getbyDestDeparture(dddto.getDestination(), dddto.getDepartureTime());
            return new Response.Builder().type(ResponseType.FLIGHT_FILTERED).data(flights).build();
        } catch (CustomException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleADD_TICKET(Request request){
        logger.debug("Adding ticket Request ...");
        Ticket ticket=(Ticket) request.data();
        try {
            server.addTicket(ticket);
            return okResponse;
        } catch (CustomException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleGET_ALL_FLIGHTS(Request request){
        logger.debug("Update Request ...");
        try {
            List<Flight> flights=server.getAllFlights();
            return new Response.Builder().type(ResponseType.GET_ALL_FLIGHTS).data(flights).build();
        } catch (CustomException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private void sendResponse(Response response) throws IOException{
        logger.debug("sending response {}",response);
        synchronized (output) {
            output.writeObject(response);
            output.flush();
        }
    }

    @Override
    public void update(List<Flight> flights) throws CustomException {
        Response resp=new Response.Builder().type(ResponseType.FLIGHT_UPDATED).build();
        logger.debug("Flight updated received ");
        try {
            sendResponse(resp);
        } catch (IOException e) {
            throw new CustomException("Sending error: "+e);
        }
    }
}
