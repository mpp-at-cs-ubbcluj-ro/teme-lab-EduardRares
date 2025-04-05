package networking;

import networking.rpcProtocol.AppClientRpcReflectionWorker;
import services.IService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.net.Socket;

public class RPCConcurrentServer extends AbsConcurrentServer{
    private IService appService;
    private static Logger logger = LogManager.getLogger(RPCConcurrentServer.class);
    public RPCConcurrentServer(int port, IService appService) {
        super(port);
        this.appService = appService;
        logger.info("Chat- ChatRpcConcurrentServer");
    }

    @Override
    protected Thread createWorker(Socket client) {
        AppClientRpcReflectionWorker worker=new AppClientRpcReflectionWorker(appService, client);

        Thread tw=new Thread(worker);
        return tw;
    }

    @Override
    public void stop(){
        logger.info("Stopping services ...");
    }
}
