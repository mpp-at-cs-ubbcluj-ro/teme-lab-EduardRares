package server;

import networking.AbstractServer;
import networking.RPCConcurrentServer;
import networking.ServerException;
import persistence.*;
import services.IService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

public class Main {
    public static int port = 55555;
    private static final Logger logger = LogManager.getLogger(Main.class);
    public static void main(String[] args) {
        Properties serverProps = new Properties();
        try {
            serverProps.load(Main.class.getResourceAsStream("/server.properties"));
            System.out.println("Server properties set. ");
            serverProps.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot find chatserver.properties " + e);
            return;
        }
        EmployeeRepoInterface employeeRepo = new EmployeeRepo(serverProps);
        FlightRepositoryInterface flightRepo = new FlightRepo(serverProps);
        TicketRepoInterface ticketRepo = new TicketRepo(serverProps);
        IService appServerImpl = new ServerImpl(employeeRepo, ticketRepo, flightRepo);

        int chatServerPort=port;
        try {
            chatServerPort = Integer.parseInt(serverProps.getProperty("chat.server.port"));
        }catch (NumberFormatException nef){
            logger.error("Wrong  Port Number"+nef.getMessage());
            logger.debug("Using default port "+port);
        }

        System.out.println("Starting server on port: " + chatServerPort);
        AbstractServer server = new RPCConcurrentServer(chatServerPort, appServerImpl);
        try {
            server.start();
        } catch (ServerException e) {
            System.err.println("Error starting the server" + e.getMessage());
        }
    }
}
