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
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class AppServicesRpcProxy implements IService {
    private String host;
    private int port;

    private static Logger logger = LogManager.getLogger(AppServicesRpcProxy.class);

    private IObserver client;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;

    private BlockingQueue<Response> qresponses;
    private volatile boolean finished;
    public AppServicesRpcProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses=new LinkedBlockingQueue<Response>();
    }



    private void closeConnection() {
        logger.debug("Closing connection");
        finished=true;
        try {
            input.close();
            output.close();
            connection.close();
            client=null;
        } catch (IOException e) {
            logger.error(e);
            logger.error(e.getStackTrace());
        }

    }

    private void sendRequest(Request request)throws CustomException {
        logger.debug("Sending request {} ",request);
        try {
            output.reset();
            output.writeObject(request);
            output.flush();
        } catch (IOException e) {
            throw new CustomException("Error sending object "+e);
        }
    }

    private Response readResponse() throws CustomException {
        Response response=null;
        try{
            response=qresponses.take();
        } catch (InterruptedException e) {
            logger.error(e);
            logger.error(e.getStackTrace());
        }
        return response;
    }
    private void initializeConnection() throws CustomException {
        try {
            logger.error(host + ":" + port);
            connection=new Socket(host,port);
            output=new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input=new ObjectInputStream(connection.getInputStream());
            finished=false;
            startReader();
        } catch (IOException e) {
            logger.error("Error initializing connection "+e);
            logger.error(e.getStackTrace());
        }
    }
    private void startReader(){
        Thread tw=new Thread(new ReaderThread());
        tw.start();
    }


    private void handleUpdate(Response response){
        if (response.type()== ResponseType.FLIGHT_UPDATED){
            List<Flight> flight= (List<Flight>) response.data();
            logger.debug("Flights in system "+flight);
            try {
                client.update();
            } catch (CustomException e) {
                logger.error(e);
                logger.error(e.getStackTrace());
            }
        }
    }

    private boolean isUpdate(Response response){
        return response.type()== ResponseType.FLIGHT_UPDATED;
    }

    @Override
    public Optional<Employee> addEmployee(Employee employee) throws CustomException {
        Request req=new Request.Builder().type(RequestType.ADD_TICKET).data(employee).build();
        sendRequest(req);
        Response response=readResponse();
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            throw new CustomException(err);
        }
        return Optional.of(employee);
    }

    @Override
    public Optional<Employee> login(String username, String password, IObserver client) throws CustomException {
        initializeConnection();
        Employee employee= new Employee(username, password);
        Request req=new Request.Builder().type(RequestType.LOGIN).data(employee).build();
        sendRequest(req);
        Response response=readResponse();
        if (response.type()== ResponseType.LOGIN){
            this.client=client;
            return Optional.of((Employee) response.data());
        }
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            closeConnection();
            throw new CustomException(err);
        }
        return Optional.empty();
    }

    @Override
    public void logout(Employee employee, IObserver client) throws CustomException {
        Request req=new Request.Builder().type(RequestType.LOGOUT).data(employee).build();
        sendRequest(req);
        Response response=readResponse();
        closeConnection();
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            throw new CustomException(err);
        }
    }

    @Override
    public void updateFlight(Flight flight) throws CustomException {
        Request req=new Request.Builder().type(RequestType.UPDATE_FLIGHT).data(flight).build();
        sendRequest(req);
        Response response=readResponse();
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            throw new CustomException(err);
        }
    }

    @Override
    public List<Flight> getbyDestDeparture(String destination, LocalDateTime departureTime) throws CustomException {
        DestDepartureDTO ddDto = new DestDepartureDTO(destination,departureTime);
        Request req=new Request.Builder().type(RequestType.GET_BY_DEST_DEPARTURE).data(ddDto).build();
        sendRequest(req);
        Response response=readResponse();
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            throw new CustomException(err);
        }
        List<Flight> flights = (List<Flight>)response.data();
        return flights;
    }

    @Override
    public List<Flight> getAllFlights() throws CustomException {
        Request req=new Request.Builder().type(RequestType.GET_ALL_FLIGHTS).build();
        sendRequest(req);
        Response response=readResponse();
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            throw new CustomException(err);
        }
        List<Flight> flights = (List<Flight>)response.data();
        return flights;
    }

    @Override
    public void addTicket(Ticket ticket) throws CustomException {
        Request req=new Request.Builder().type(RequestType.ADD_TICKET).data(ticket).build();
        sendRequest(req);
        Response response=readResponse();
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            throw new CustomException(err);
        }
    }

    private class ReaderThread implements Runnable{
        public void run() {
            while(!finished){
                try {
                    Object response=input.readObject();
                    logger.debug("response received "+response);
                    if (isUpdate((Response)response)){
                        handleUpdate((Response)response);
                    }else{
                        try {
                            qresponses.put((Response)response);
                        } catch (InterruptedException e) {
                            logger.error(e);
                            logger.error(e.getStackTrace());
                        }
                    }
                } catch (IOException|ClassNotFoundException e) {
                    logger.error("Reading error "+e);
                }
            }
        }
    }
}
