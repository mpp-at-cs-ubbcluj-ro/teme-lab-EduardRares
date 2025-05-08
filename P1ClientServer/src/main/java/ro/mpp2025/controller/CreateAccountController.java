package ro.mpp2025.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import ro.mpp2025.model.Employee;
import ro.mpp2025.service.EmployeeService;

public class CreateAccountController {

    @FXML
    private TextField usernameText;
    @FXML
    private TextField passwordText;
    @FXML
    private TextField emailText;
    @FXML
    private Label errorLabel;
    private EmployeeService employeeService;

    public void setService(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @FXML
    private void handleCreate() {
        errorLabel.setText("");
        if(usernameText.getText().isEmpty() || passwordText.getText().isEmpty() || emailText.getText().isEmpty()) {
            errorLabel.setText("All fields are required");
        }
        else {
            Employee employee = new Employee(usernameText.getText(), passwordText.getText(), emailText.getText());
            if (employeeService.add(employee).isEmpty()) {
                errorLabel.setText("This email already exists");
            }
        }
    }
}
