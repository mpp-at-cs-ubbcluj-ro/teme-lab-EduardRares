package networking.protobuffprotocol;

import model.*;
import networking.dto.DestDepartureDTO;
import services.CustomException;
import services.IObserver;
import services.IService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;


public class ProtoAppWorker implements Runnable, IObserver {
    private IService server;
     private Socket connection;

     private InputStream input;
     private OutputStream output;
     private volatile boolean connected;
     public ProtoAppWorker(IService server, Socket connection) {
         this.server = server;
         this.connection = connection;
         try{
             output=connection.getOutputStream() ;//new ObjectOutputStream(connection.getOutputStream());
             input=connection.getInputStream(); //new ObjectInputStream(connection.getInputStream());
             connected=true;
         } catch (IOException e) {
             e.printStackTrace();
         }
     }

     public void run() {

         while(connected){
             try {
                // Object request=input.readObject();
                 System.out.println("Waiting requests ...");
                 AppProtobufs.AppRequest request=AppProtobufs.AppRequest.parseDelimitedFrom(input);
                 System.out.println("Request received: "+request);
                 AppProtobufs.AppResponse response=handleRequest(request);
                 if (response!=null){
                    sendResponse(response);
                 }
             } catch (IOException e) {
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

     private AppProtobufs.AppResponse handleRequest(AppProtobufs.AppRequest request){
         AppProtobufs.AppResponse response=null;
         switch (request.getType()){
             case LOGIN:{
                 System.out.println("Login request ...");
                 Employee user=ProtoUtils.getUser(request);
                 try {
                     server.login(user.getUsername(), user.getPassword(), this);
                     return ProtoUtils.createOkResponse();
                 } catch (CustomException e) {
                     connected=false;
                     return ProtoUtils.createErrorResponse(e.getMessage());
                 }
             }
             case LOGOUT:{
                 System.out.println("Logout request");
                 Employee user=ProtoUtils.getUser(request);
                 try {
                     server.logout(user, this);
                     connected=false;
                     return ProtoUtils.createOkResponse();

                 } catch (CustomException e) {
                     return ProtoUtils.createErrorResponse(e.getMessage());
                 }
             }
             case UPDATE_FLIGHT: {
                 System.out.println("Update flight request");
                 Flight flight=ProtoUtils.getFlight(request);
                 try {
                     server.updateFlight(flight);
                     return ProtoUtils.createOkResponse();
                 }
                 catch (CustomException e) {
                     return ProtoUtils.createErrorResponse(e.getMessage());
                 }
             }
             case GET_BY_DEST_DEPARTURE: {
                System.out.println("Get by departure request");
                 DestDepartureDTO destDepartureDTO = ProtoUtils.getDestDepartureDTO(request);
                 try {
                     List<Flight> flights = server.getbyDestDeparture(destDepartureDTO.getDestination(), destDepartureDTO.getDepartureTime());
                     return ProtoUtils.createFlightFilteredResponse(flights);
                 }
                 catch (CustomException e) {
                     return ProtoUtils.createErrorResponse(e.getMessage());
                 }
             }
             case GET_ALL_FLIGHTS: {
                System.out.println("Get all flights request");
                try {
                    List<Flight> flights = server.getAllFlights();
                    return ProtoUtils.createGetAllFlightsResponse(flights);
                }
                catch (CustomException e) {
                    return ProtoUtils.createErrorResponse(e.getMessage());
                }
             }
             case ADD_TICKET: {
                 System.out.println("Add ticket request");
                 Ticket ticket=ProtoUtils.getTicket(request);
                 try {
                     server.addTicket(ticket);
                     return ProtoUtils.createOkResponse();
                 }
                 catch (CustomException e) {
                     return ProtoUtils.createErrorResponse(e.getMessage());
                 }
             }
         }
         return response;
     }

     private void sendResponse(AppProtobufs.AppResponse response) throws IOException{
         System.out.println("sending response "+response);
         synchronized (output) {
             response.writeDelimitedTo(output);
             //output.writeObject(response);
             output.flush();
         }
     }

    @Override
    public void update(List<Flight> flights) throws CustomException {
        try {
            sendResponse(ProtoUtils.createFlightUpdateResponse());
        } catch (CustomException e) {
            throw new CustomException(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
