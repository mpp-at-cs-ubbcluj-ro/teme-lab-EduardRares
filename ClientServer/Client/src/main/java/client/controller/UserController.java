package client.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Employee;
import model.Flight;
import model.Ticket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.CustomException;
import services.IObserver;
import services.IService;
import java.time.Instant;

import java.net.URL;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class UserController implements Initializable, IObserver {
    @FXML
    private TableColumn<Flight, String> tableColumnDestinatie;
    @FXML
    private TableColumn<Flight, String> tableColumnData;
    @FXML
    private TableColumn<Flight, String> tableColumnOra;
    @FXML
    private TableColumn<Flight, String> tableColumnAeroport;
    @FXML
    private TableColumn<Flight, Integer> tableColumnLocuri;
    private IService service;;
    private Employee user;
    @FXML
    private TextField textDestinatie;
    @FXML
    private DatePicker dateData;
    @FXML
    private TableView<Flight> tableViewFlights;
    @FXML
    private Button searchButton;
    @FXML
    private Button buyButton;
    private ObservableList<Flight> model = FXCollections.observableArrayList();
    private Flight selectedFlight;
    private static Logger logger = LogManager.getLogger(UserController.class);

    public void setService(IService service) {
        this.service = service;
    }

    @FXML
    private void handleFlights() {
        dateData.setVisible(false);
        textDestinatie.setVisible(false);
        searchButton.setVisible(false);
        tableViewFlights.setVisible(true);
        buyButton.setVisible(false);
        model.clear();
        model.addAll(service.getAllFlights());
        tableViewFlights.setItems(model);
        logger.debug("INIT : am in lista de zboruri " + model.size());
    }

    @FXML
    private void handleFilter() {
        dateData.setVisible(true);
        textDestinatie.setVisible(true);
        searchButton.setVisible(true);
        tableViewFlights.setVisible(true);
        buyButton.setVisible(false);
        model.clear();
    }

    @FXML
    private void handleSearch() {
        model.clear();
        buyButton.setVisible(true);
        if(!textDestinatie.getText().isEmpty() && !dateData.getValue().toString().isEmpty()) {
            model.addAll(service.getbyDestDeparture(textDestinatie.getText(), dateData.getValue().atStartOfDay()));
            tableViewFlights.setItems(model);
        }
    }

    @FXML
    private void handleBuy() {
        if(selectedFlight != null) {
            Optional<Ticket> e = TakeInfoAlert.takeInfoTicket(new Ticket(), selectedFlight);
            if(e.isPresent()) {
                service.addTicket(e.get());
                e.get().getFlight().setNumberOfAvailableSeats(e.get().getFlight().getNumberOfAvailableSeats() - e.get().getNoOfTickets());
                service.updateFlight(e.get().getFlight());
            }
        }
    }

    public void handleLogOut(ActionEvent actionEvent) {
        logout();
        ((Node)(actionEvent.getSource())).getScene().getWindow().hide();
    }

    void logout() {
        try {
            service.logout(user, this);
        } catch (CustomException e) {
            logger.error("Logout error " + e);
        }
    }

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.debug("INIT : am in lista de zboruri " + model.size());
        logger.debug("END INIT!!!!!!!!!");
        dateData.setVisible(false);
        textDestinatie.setVisible(false);
        searchButton.setVisible(false);
        tableViewFlights.setVisible(false);
        buyButton.setVisible(false);
        tableColumnDestinatie.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDestination())
        );
        tableColumnData.setCellValueFactory(cellData ->
                new SimpleStringProperty(Instant.ofEpochMilli(cellData.getValue().getDepartureTimeMillis()).atZone(ZoneId.systemDefault()).toLocalDate().toString())
        );
        tableColumnOra.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDepartureTime().toString())
        );
        tableColumnAeroport.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getAirport()));
        tableColumnLocuri.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getNumberOfAvailableSeats()));
        tableViewFlights.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedFlight = tableViewFlights.getSelectionModel().getSelectedItem();
        });
    }

    @Override
    public void update(List<Flight> flights) throws CustomException {
        Platform.runLater(this::handleFlights);
    }

    public void setUser(Employee user) {
        this.user = user;
        handleFlights();
    }
}
