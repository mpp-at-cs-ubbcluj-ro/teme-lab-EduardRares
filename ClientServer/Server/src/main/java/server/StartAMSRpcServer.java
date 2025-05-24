package server;

import networking.AbstractServer;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class StartAMSRpcServer {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring-Server.xml");
        AbstractServer server = context.getBean("appTCPServer", AbstractServer.class);
        try {
            server.start();
        }
        catch (Exception e) {
            System.err.println("Error starting server " + e.getMessage());
        }
    }
}
