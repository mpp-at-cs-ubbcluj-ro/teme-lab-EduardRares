package ro.mpp2025.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ro.mpp2025.Main;
import ro.mpp2025.model.Employee;
import ro.mpp2025.service.EmployeeService;
import ro.mpp2025.service.FlightService;
import ro.mpp2025.service.TicketService;

import java.io.IOException;
import java.util.Optional;

public class LoginController {
    private EmployeeService employeeService;
    private FlightService flightService;
    private TicketService ticketService;
    @FXML
    private TextField emailText;
    @FXML
    private TextField passwordText;
    private Stage currStage;

    public void setService(EmployeeService employeeService, FlightService flightService, TicketService ticketService) {
        this.employeeService = employeeService;
        this.flightService = flightService;
        this.ticketService = ticketService;
    }

    public void setStage(Stage stage) {
        this.currStage = stage;
    }

    @FXML
    private void login(ActionEvent actionEvent) throws IOException {
        String email = emailText.getText();
        String password = passwordText.getText();
        Optional<Employee> employee = employeeService.login(email, password);
        if (employee.isPresent()) {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/UserInterface.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle("User Interface");
            stage.setScene(scene);
            stage.show();
            UserController userController = fxmlLoader.getController();
            userController.setService(employee.get(), employeeService, flightService, ticketService);
            userController.setStage(stage);
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No such employee");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleCreate() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/CreateAccInterface.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        CreateAccountController createAccountController = fxmlLoader.getController();
        createAccountController.setService(employeeService);
        stage.setTitle("Create Interface");
        stage.setScene(scene);
        stage.showAndWait();
    }
}
