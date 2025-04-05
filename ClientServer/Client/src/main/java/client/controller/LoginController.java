package client.controller;

import client.Main;
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

public class LoginController {
    private IService service;
    private UserController userController;
    private Parent mainChatParent;
    private static Logger logger = LogManager.getLogger(LoginController.class);
    @FXML
    private TextField emailText;
    @FXML
    private TextField passwordText;
    private Stage currStage;

    public void setService(IService service) {
        this.service = service;
    }

    public void setStage(Stage stage) {
        this.currStage = stage;
    }

    public void setChatController(UserController userController) {
        this.userController = userController;
    }

    public void setParent(Parent p){
        mainChatParent = p;
    }

    @FXML
    private void login(ActionEvent actionEvent) throws IOException {
        String email = emailText.getText();
        String password = passwordText.getText();
        Optional<Employee> employee = service.login(email, password, userController);
        if (employee.isPresent()) {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/UserInterface.fxml"));
            Stage stage=new Stage();
            stage.setTitle("Chat Window for " +employee.get().getId());
            stage.setScene(new Scene(mainChatParent));
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    userController.logout();
                    logger.debug("Closing application");
                    System.exit(0);
                }
            });

            stage.show();
            userController.setUser(employee.get());
            ((Node)(actionEvent.getSource())).getScene().getWindow().hide();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No such employee");
            alert.showAndWait();
        }
    }

//    @FXML
//    private void handleCreate() throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/CreateAccInterface.fxml"));
//        Scene scene = new Scene(fxmlLoader.load());
//        Stage stage = new Stage();
//        CreateAccountController createAccountController = fxmlLoader.getController();
//        createAccountController.setService(employeeService);
//        stage.setTitle("Create Interface");
//        stage.setScene(scene);
//        stage.showAndWait();
//    }
}
