package client;

import client.controller.LoginController;
import client.controller.UserController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import networking.protobuffprotocol.ProtoAppProxy;
import networking.rpcProtocol.AppServicesRpcProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.IService;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class Main extends Application {
    private Stage primaryStage;
    private static int defaultChatPort = 55555;
    private static String defaultServer = "localhost";
    private static Logger logger = LogManager.getLogger(Main.class);

    @Override
    public void start(Stage primaryStage) throws Exception {
        logger.debug("In start");
        Properties clientProps = new Properties();
        try {
            clientProps.load(Main.class.getResourceAsStream("/chatclient.properties"));
            logger.info("Client properties set {} ", clientProps);
            // clientProps.list(System.out);
        } catch (IOException e) {
            logger.error("Cannot find chatclient.properties " + e);
            logger.debug("Looking into folder {}", (new File(".")).getAbsolutePath());
            return;
        }
        String serverIP = clientProps.getProperty("chat.server.host", defaultServer);

        int serverPort=defaultChatPort;
        try{
            serverPort=Integer.parseInt(clientProps.getProperty("chat.server.port"));
        }catch(NumberFormatException ex){
            System.err.println("Wrong port number " + ex.getMessage());
            System.out.println("Using default port: " + defaultChatPort);
        }

        logger.info("Using server IP " + serverIP);
        logger.info("Using server port " + serverPort);

        IService server = new ProtoAppProxy(serverIP, serverPort);

        FXMLLoader loader = new FXMLLoader(
                getClass().getClassLoader().getResource("LoginInterface.fxml"));
        Parent root=loader.load();
        LoginController ctrl =
                loader.<LoginController>getController();
        ctrl.setService(server);

        FXMLLoader cloader = new FXMLLoader(
                getClass().getClassLoader().getResource("UserInterface.fxml"));
        Parent croot=cloader.load();
        UserController chatCtrl =
                cloader.<UserController>getController();
        chatCtrl.setService(server);

        ctrl.setChatController(chatCtrl);
        ctrl.setParent(croot);

        primaryStage.setTitle("MPP chat");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}