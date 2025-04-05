package networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class AbstractServer {
    private int port;
    private ServerSocket serverSocket;

    public AbstractServer(int port) {
        this.port = port;
    }

    public void start() throws ServerException {
        try{
            serverSocket=new ServerSocket(port);
            while(true){
                System.out.println("Waiting for clients ...");
                Socket client=serverSocket.accept();
                System.out.println("Client connected ...");
                processRequest(client);
            }
        } catch (IOException e) {
            throw new ServerException("Starting server errror ",e);
        }finally {
            try{
                serverSocket.close();
            } catch (IOException e) {
                throw new ServerException("Closing server error ", e);
            }
        }
    }

    protected abstract  void processRequest(Socket client);

    public void stop() throws ServerException {
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new ServerException("Closing server error ", e);
        }
    }
}
