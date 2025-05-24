package networking.protobuffprotocol;

import com.google.protobuf.ServiceException;
import model.Employee;
import model.Flight;
import model.Ticket;
import networking.ServerException;
import services.CustomException;
import services.IObserver;
import services.IService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ProtoAppProxy implements IService {
    private String host;
      private int port;

      private IObserver client;

      private InputStream input;
      private OutputStream output;
      private Socket connection;

      private BlockingQueue<AppProtobufs.AppResponse> qresponses;
      private volatile boolean finished;
      public ProtoAppProxy(String host, int port) {
          this.host = host;
          this.port = port;
          qresponses=new LinkedBlockingQueue<AppProtobufs.AppResponse>();
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

      private void sendRequest(AppProtobufs.AppRequest request)throws CustomException {
          try {
              System.out.println("Sending request ..."+request);
              //request.writeTo(output);
              request.writeDelimitedTo(output);
              output.flush();
              System.out.println("Request sent.");
          } catch (IOException e) {
              throw new CustomException("Error sending object "+e);
          }

      }

      private AppProtobufs.AppResponse readResponse() throws CustomException{
          AppProtobufs.AppResponse response=null;
          try{
              response=qresponses.take();

          } catch (InterruptedException e) {
              e.printStackTrace();
          }
          return response;
      }
      private void initializeConnection() throws CustomException{
           try {
              connection=new Socket(host,port);
              output=connection.getOutputStream();
              output.flush();
              input=connection.getInputStream();     //new ObjectInputStream(connection.getInputStream());
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


      private void handleUpdate(AppProtobufs.AppResponse updateResponse){
          switch (updateResponse.getType()){
              case FLIGHT_UPDATED:{
                  List<Flight> flights = ProtoUtils.getFlights(updateResponse);
                  client.update(flights);
                  break;
              }
          }

      }

    @Override
    public Optional<Employee> addEmployee(Employee employee) throws CustomException {
        return Optional.empty();
    }

    @Override
    public Optional<Employee> login(String username, String password, IObserver client) throws CustomException {
        initializeConnection();
        System.out.println("Login request ...");
        Employee employee = new Employee();
        employee.setUsername(username);
        employee.setPassword(password);
        sendRequest(ProtoUtils.createLoginRequest(employee));
        AppProtobufs.AppResponse response=readResponse();
        if (response.getType()==AppProtobufs.AppResponse.Type.OK){
            this.client=client;
            return Optional.of(employee);
        }
        if (response.getType()==AppProtobufs.AppResponse.Type.ERROR){
            String errorText=ProtoUtils.getError(response);
            closeConnection();
            throw new CustomException(errorText);
        }
        return Optional.empty();
    }

    @Override
    public void logout(Employee employee, IObserver client) throws CustomException {
        sendRequest(ProtoUtils.createLogoutRequest(employee));
        AppProtobufs.AppResponse response=readResponse();
        closeConnection();
        if (response.getType()==AppProtobufs.AppResponse.Type.ERROR){
            String errorText=ProtoUtils.getError(response);
            throw new CustomException(errorText);
        }
    }

    @Override
    public void updateFlight(Flight flight) throws CustomException {
          sendRequest(ProtoUtils.createUpdateFlightRequest(flight));
          AppProtobufs.AppResponse response=readResponse();
          if(response.getType()==AppProtobufs.AppResponse.Type.ERROR){
              String errorText=ProtoUtils.getError(response);
              throw new CustomException(errorText);
          }
    }

    @Override
    public List<Flight> getbyDestDeparture(String destination, LocalDateTime departureTime) throws CustomException {
        sendRequest(ProtoUtils.createDestDepartureRequest(destination,departureTime));
        AppProtobufs.AppResponse response=readResponse();
        if(response.getType()==AppProtobufs.AppResponse.Type.ERROR){
            String errorText=ProtoUtils.getError(response);
            throw new CustomException(errorText);
        }
        return ProtoUtils.getFlights(response);
    }

    @Override
    public List<Flight> getAllFlights() throws CustomException {
          sendRequest(ProtoUtils.createGetAllFLightsRequest());
          AppProtobufs.AppResponse response=readResponse();
          if(response.getType()==AppProtobufs.AppResponse.Type.ERROR){
              String errorText=ProtoUtils.getError(response);
              throw new CustomException(errorText);
          }
          return ProtoUtils.getFlights(response);
    }

    @Override
    public void addTicket(Ticket ticket) throws CustomException {
        sendRequest(ProtoUtils.createAddTicketRequest(ticket));
        AppProtobufs.AppResponse response=readResponse();
        if(response.getType()==AppProtobufs.AppResponse.Type.ERROR){
            String errorText=ProtoUtils.getError(response);
            throw new CustomException(errorText);
        }
    }

    private class ReaderThread implements Runnable{
          public void run() {
              while(!finished){
                  try {
                      AppProtobufs.AppResponse response=AppProtobufs.AppResponse.parseDelimitedFrom(input);
                      System.out.println("response received "+response);

                      if (isUpdateResponse(response.getType())){
                           handleUpdate(response);
                      }else{
                          try {
                              qresponses.put(response);
                          } catch (InterruptedException e) {
                              e.printStackTrace();
                          }
                      }
                  } catch (IOException e) {
                      System.out.println("Reading error "+e);
                  }
              }
          }
      }

    private boolean isUpdateResponse(AppProtobufs.AppResponse.Type type){
        switch (type){
            case FLIGHT_UPDATED:  return true;
        }
        return false;
    }
}
