package ro.mpp2025.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import ro.mpp2025.model.Employee;
import ro.mpp2025.model.Flight;
import ro.mpp2025.model.Ticket;
import ro.mpp2025.service.EmployeeService;
import ro.mpp2025.service.FlightService;
import ro.mpp2025.service.TicketService;
import ro.mpp2025.utils.events.Event;
import ro.mpp2025.utils.observer.Observer;

import java.util.Optional;

public class UserController implements Observer<Event> {
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
    private EmployeeService employeeService;
    private FlightService flightService;
    private TicketService ticketService;
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
    private Stage stage;

    public void setService(Employee user, EmployeeService employeeService, FlightService flightService, TicketService ticketService) {
        this.user = user;
        this.employeeService = employeeService;
        this.flightService = flightService;
        this.ticketService = ticketService;
        flightService.addObserver(this);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void initialize() {
        dateData.setVisible(false);
        textDestinatie.setVisible(false);
        searchButton.setVisible(false);
        tableViewFlights.setVisible(false);
        buyButton.setVisible(false);
        tableColumnDestinatie.setCellValueFactory(new PropertyValueFactory<>("destination"));
        tableColumnData.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDepartureTime().toLocalDate().toString())
        );
        tableColumnOra.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDepartureTime().toLocalTime().toString())
        );
        tableColumnAeroport.setCellValueFactory(new PropertyValueFactory<>("airport"));
        tableColumnLocuri.setCellValueFactory(new PropertyValueFactory<>("numberOfAvailableSeats"));
        tableViewFlights.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedFlight = tableViewFlights.getSelectionModel().getSelectedItem();
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
        model.addAll(flightService.getAllFlights());
        tableViewFlights.setItems(model);
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
            model.addAll(flightService.getbyDestDeparture(textDestinatie.getText(), dateData.getValue().atStartOfDay()));
            tableViewFlights.setItems(model);
        }
    }

    @FXML
    private void handleBuy() {
        if(selectedFlight != null) {
            Optional<Ticket> e = TakeInfoAlert.takeInfoTicket(new Ticket(), selectedFlight);
            if(e.isPresent()) {
                ticketService.add(e.get());
                e.get().getFlight().setNumberOfAvailableSeats(e.get().getFlight().getNumberOfAvailableSeats() - e.get().getNoOfTickets());
                flightService.updateFlight(e.get().getFlight());
            }
        }
    }

    @Override
    public void update(Event event) {
        handleFlights();
    }

    @FXML
    private void handleLogOut() {
        stage.close();
    }
}
