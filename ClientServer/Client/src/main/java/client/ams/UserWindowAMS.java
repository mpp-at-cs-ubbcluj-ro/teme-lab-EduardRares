package client.ams;

import client.controller.UserController;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import model.Employee;
import model.Flight;
import model.Ticket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.CustomException;
import services.IService;

import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;
import java.util.ResourceBundle;

public class UserWindowAMS implements Initializable {
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
    private ClientCtrlAMS clientCtrl;

    public void setClientCtrl(ClientCtrlAMS clientCtrl) {
        this.clientCtrl = clientCtrl;
    }

    public void setChatController(ClientCtrlAMS ctrl) {
        clientCtrl = ctrl;
        ObservableList<Flight> sharedModel = clientCtrl.getFlightsModel();
        tableViewFlights.setItems(sharedModel);
        clientCtrl.setOnNotification(notification -> {
            handleFlights();
        });
    }
    @FXML
    private void handleFlights() {
        dateData.setVisible(false);
        textDestinatie.setVisible(false);
        searchButton.setVisible(false);
        tableViewFlights.setVisible(true);
        buyButton.setVisible(false);
        model.clear();
        model.addAll(clientCtrl.getFlightsModel());
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
            model.addAll(clientCtrl.getFilteredFlightsModel(textDestinatie.getText(), dateData.getValue().atStartOfDay()));
            tableViewFlights.setItems(model);
        }
    }

    @FXML
    private void handleBuy() {
        if(selectedFlight != null) {
            Optional<Ticket> e = TakeInfoAlert.takeInfoTicket(new Ticket(), selectedFlight);
            if(e.isPresent()) {
                clientCtrl.addTicket(e.get());
                e.get().getFlight().setNumberOfAvailableSeats(e.get().getFlight().getNumberOfAvailableSeats() - e.get().getNoOfTickets());
                clientCtrl.updateFlight(e.get().getFlight());
            }
        }
    }

    public void handleLogOut(ActionEvent actionEvent) {
        logout();
        ((Node)(actionEvent.getSource())).getScene().getWindow().hide();
    }

    void logout() {
        try {
            clientCtrl.logout();
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
        tableColumnDestinatie.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getDestination()));
        tableColumnData.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(
                        Instant.ofEpochMilli(c.getValue().getDepartureTimeMillis())
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate().toString()));
        tableColumnOra.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getDepartureTime().toString()));
        tableColumnAeroport.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getAirport()));
        tableColumnLocuri.setCellValueFactory(c ->
                new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getNumberOfAvailableSeats()));
        tableViewFlights.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedFlight = tableViewFlights.getSelectionModel().getSelectedItem();
        });
    }

    public void setUser(Employee user) {
        this.user = user;
        handleFlights();
    }
}