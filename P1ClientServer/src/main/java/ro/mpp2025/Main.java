package ro.mpp2025;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ro.mpp2025.Repository.EmployeeRepo;
import ro.mpp2025.Repository.EmployeeRepoInterface;
import ro.mpp2025.Repository.FlightRepo;
import ro.mpp2025.Repository.TicketRepo;
import ro.mpp2025.controller.LoginController;
import ro.mpp2025.model.Employee;
import ro.mpp2025.service.EmployeeService;
import ro.mpp2025.service.FlightService;
import ro.mpp2025.service.TicketService;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.Properties;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main extends Application {
    public static void main(String[] args) {
        launch();
    }

    private EmployeeService employeeService;
    private FlightService flightService;
    private TicketService ticketService;
    @Override
    public void start(Stage stage) throws Exception {
        Properties props=new Properties();
        try {
            props.load(new FileReader("bd.config"));
        } catch (IOException e) {
            System.out.println("Cannot find bd.config "+e);
        }
        employeeService = new EmployeeService(new EmployeeRepo(props));
        flightService = new FlightService(new FlightRepo(props));
        ticketService = new TicketService(new TicketRepo(props));

        initLoginView(stage);
    }

    private void initLoginView(Stage stage) throws IOException {
        System.out.println(FileSystems.getDefault()
                .getPath("")
                .toAbsolutePath()
                .toString());
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/LoginInterface.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
        LoginController loginController = fxmlLoader.getController();
        loginController.setService(employeeService, flightService, ticketService);
        loginController.setStage(stage);
    }
}