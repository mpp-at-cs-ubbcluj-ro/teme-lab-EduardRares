package networking;

import networking.protobuffprotocol.ProtoAppWorker;
import services.IService;

import java.net.Socket;

public class AppProtobuffConcurrentServer extends AbsConcurrentServer{
    private IService service;
    public AppProtobuffConcurrentServer(int port, IService service) {
        super(port);
        this.service = service;
        System.out.println("Chat- ChatProtobuffConcurrentServer");
    }

    @Override
    protected Thread createWorker(Socket client) {
        ProtoAppWorker worker=new ProtoAppWorker(service,client);
        Thread tw=new Thread(worker);
        return tw;
    }
}
