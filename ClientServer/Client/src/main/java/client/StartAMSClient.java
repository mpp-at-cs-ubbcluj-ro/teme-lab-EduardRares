package client;

import client.ams.ClientCtrlAMS;
import client.ams.LoginWindowAMS;
import client.ams.UserWindowAMS;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class StartAMSClient extends Application {
    public void start(Stage primarystage) throws IOException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring-client.xml");
        ClientCtrlAMS ctrl=context.getBean("chatCtrl",ClientCtrlAMS.class);
        FXMLLoader loader = new FXMLLoader(
                getClass().getClassLoader().getResource("LoginInterfaceAMS.fxml"));
        Parent root=loader.load();
        LoginWindowAMS controller=loader.getController();
        controller.setChatController(ctrl);

        FXMLLoader cloader = new FXMLLoader(
                getClass().getClassLoader().getResource("UserInterfaceAMS.fxml"));
        Parent croot=cloader.load();
        UserWindowAMS controller1=cloader.getController();
        controller1.setChatController(ctrl);
        controller.setUserWindow(controller1);
        controller.setParent(croot);
        primarystage.setTitle("MPP chat");
        primarystage.setScene(new Scene(root));
        primarystage.show();
    }
}
