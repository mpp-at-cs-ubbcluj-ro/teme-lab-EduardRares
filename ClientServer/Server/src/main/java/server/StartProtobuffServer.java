package server;

import networking.AbstractServer;
import networking.AppProtobuffConcurrentServer;
import networking.RPCConcurrentServer;
import networking.ServerException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import persistence.*;
import services.CustomException;
import services.IService;

import java.io.IOException;
import java.util.Properties;


public class StartProtobuffServer {
    private static int defaultPort=55555;
    private static final Logger logger = LogManager.getLogger(StartProtobuffServer.class);
    public static void main(String[] args) {
        Properties serverProps=new Properties();
        try {
            serverProps.load(Main.class.getResourceAsStream("/server.properties"));
            System.out.println("Server properties set. ");
            serverProps.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot find server.properties "+e);
            return;
        }
        EmployeeRepoInterface employeeRepo = new EmployeeHibernateRepository();
        FlightRepositoryInterface flightRepo = new FlightHibernateRepository();
        TicketRepoInterface ticketRepo = new TicketRepo(serverProps);
        IService appServerImpl = new ServerImpl(employeeRepo, ticketRepo, flightRepo);

        int chatServerPort=defaultPort;
        try {
            chatServerPort = Integer.parseInt(serverProps.getProperty("chat.server.port"));
        }catch (NumberFormatException nef){
            System.err.println("Wrong  Port Number"+nef.getMessage());
            System.err.println("Using default port "+defaultPort);
        }
        System.out.println("Starting server on port: "+chatServerPort);
        AbstractServer server = new AppProtobuffConcurrentServer(chatServerPort, appServerImpl);
        try {
            server.start();
        } catch (ServerException e) {
            System.err.println("Error starting the server" + e.getMessage());
        }

    }
}
