package networking;

import networking.rpcProtocol.ams.AppClientAMSRpcReflectionWorker;
import services.IServiceAMS;

import java.net.Socket;

/**
 * Created by grigo on 5/2/17.
 */
public class ChatRpcAMSConcurrentServer extends AbsConcurrentServer {
    private IServiceAMS server;
    public ChatRpcAMSConcurrentServer(int port, IServiceAMS server) {
        super(port);
        this.server = server;
        System.out.println("Chat- ChatRpcAMSConcurrentServer port "+port);
    }

    @Override
    protected Thread createWorker(Socket client) {
        AppClientAMSRpcReflectionWorker worker=new AppClientAMSRpcReflectionWorker(server, client);

        Thread tw=new Thread(worker);
        return tw;
    }
}
