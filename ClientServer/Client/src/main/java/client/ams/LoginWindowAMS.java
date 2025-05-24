package client.ams;


import client.StartAMSClient;
import client.controller.LoginController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.Employee;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.IService;

import java.io.IOException;
import java.util.Optional;

public class LoginWindowAMS {
    private IService service;
    private ClientCtrlAMS ctrl;
    private Parent mainChatParent;
    private static Logger logger = LogManager.getLogger(LoginController.class);
    @FXML
    private TextField emailText;
    @FXML
    private TextField passwordText;
    private UserWindowAMS userWindowAMS;


    public void setChatController(ClientCtrlAMS userController) {
        this.ctrl = userController;
    }

    public void setUserWindow(UserWindowAMS userWindow) {
        this.userWindowAMS = userWindow;
    }


    public void setParent(Parent p) {
        mainChatParent = p;
    }

    @FXML
    private void login(ActionEvent actionEvent) throws IOException {
        String email = emailText.getText();
        String password = passwordText.getText();
        Optional<Employee> employee = ctrl.login(email, password);
        if (employee.isPresent()) {
            FXMLLoader fxmlLoader = new FXMLLoader(StartAMSClient.class.getResource("/UserInterfaceAMS.fxml"));
            Stage stage = new Stage();

            stage.setTitle("Chat Window for " + employee.get().getUsername());
            userWindowAMS.setUser(employee.get());
            stage.setScene(new Scene(mainChatParent));
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    ctrl.logout();
                    logger.debug("Closing application");
                    System.exit(0);
                }
            });

            stage.show();
            ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No such employee");
            alert.showAndWait();
        }
    }
}