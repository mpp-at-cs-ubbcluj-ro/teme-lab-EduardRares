package networking.rpcProtocol.ams;

import model.Employee;
import model.Flight;
import model.Ticket;
import networking.dto.DestDepartureDTO;
import networking.rpcProtocol.Request;
import networking.rpcProtocol.RequestType;
import networking.rpcProtocol.Response;
import networking.rpcProtocol.ResponseType;
import services.CustomException;
import services.IClient;
import services.IObserver;
import services.IServiceAMS;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by grigo on 12/15/15.
 */
public class AppServerAMSRpcProxy implements IServiceAMS {
    private String host;
    private int port;

    private IClient client;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;

    private BlockingQueue<Response> qresponses;
    private volatile boolean finished;
    public AppServerAMSRpcProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses=new LinkedBlockingQueue<Response>();
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
    public Optional<Employee> login(String username, String password) throws CustomException {
        initializeConnection();
        Employee employee= new Employee(username, password);
        Request req=new Request.Builder().type(RequestType.LOGIN).data(employee).build();
        sendRequest(req);
        Response response=readResponse();
        if (response.type()== ResponseType.LOGIN){
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
    public void logout(Employee employee) throws CustomException {
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

    private void closeConnection() {
        finished=true;
        try {
            input.close();
            output.close();
            connection.close();
            client=null;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void sendRequest(Request request)throws CustomException {
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
            /*synchronized (responses){
                responses.wait();
            }
            response = responses.remove(0);    */
            response=qresponses.take();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }
    private void initializeConnection() throws CustomException {
        try {
            connection=new Socket(host,port);
            output=new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input=new ObjectInputStream(connection.getInputStream());
            finished=false;
            startReader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void startReader(){
        Thread tw=new Thread(new ReaderThread());
        tw.start();
    }




    private boolean isUpdate(Response response){
        return response.type()== ResponseType.FLIGHT_UPDATED;
    }
    private class ReaderThread implements Runnable{
        public void run() {
            while(!finished){
                try {
                    Object response=input.readObject();
                    System.out.println("response received "+response);
                    if (isUpdate((Response)response)){
                    }else{

                        try {
                            qresponses.put((Response)response);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException|ClassNotFoundException e) {
                    System.out.println("Reading error "+e);
                }
            }
        }
    }
}
