package networking;

import java.io.IOException;
import java.net.Socket;

public abstract class AbsConcurrentServer extends AbstractServer {
    public AbsConcurrentServer(int port) {
        super(port);
    }

    protected void processRequest(Socket client) {
        Thread thread = createWorker(client);
        thread.start();
    }

    protected abstract Thread createWorker(Socket client);
}
