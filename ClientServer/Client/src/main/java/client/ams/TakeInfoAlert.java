package client.ams;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Flight;
import model.Ticket;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TakeInfoAlert {
    static Optional<Ticket> takeInfoTicket(Ticket ticket, Flight flight) {
        List<String> names = new ArrayList<>();
        Label name = new Label();
        name.setText("Names");
        Label numberOfSeats = new Label();
        numberOfSeats.setText("Number of Seats");
        TextField nameField = new TextField();
        TextField numberOfSeatsField = new TextField();
        numberOfSeatsField.setEditable(false);
        numberOfSeatsField.setPromptText(names.size() + " Seats");
        Button submitButton = new Button();
        submitButton.setText("Buy");
        Button addButton = new Button();
        addButton.setText("Add");
        VBox pane = new VBox();
        Scene scene = new Scene(pane, 400, 300);
        pane.setAlignment(Pos.CENTER);
        pane.getChildren().addAll(name, nameField, addButton, new Label(), numberOfSeats, numberOfSeatsField, new Label(), new Label(), submitButton);
        Stage stage = new Stage();
        stage.setScene(scene);
        final Optional<Ticket>[] result = new Optional[]{Optional.empty()};

        addButton.setOnAction(e -> {
            names.add(nameField.getText());
            nameField.clear();
            numberOfSeatsField.setPromptText(names.size() + " Seats");
        });
        submitButton.setOnAction(e -> {
            if(flight.getNumberOfAvailableSeats() >= names.size()) {
                ticket.setNames(names);
                ticket.setNoOfTickets(names.size());
                ticket.setFlight(flight);
                result[0] = Optional.of(ticket);
                stage.close();
            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Number of Seats exceeded");
                alert.showAndWait();
            }
        });

        stage.showAndWait();
        return result[0];
    }
}
