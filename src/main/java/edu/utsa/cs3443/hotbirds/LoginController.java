package edu.utsa.cs3443.hotbirds;

import edu.utsa.cs3443.hotbirds.model.User;
import edu.utsa.cs3443.hotbirds.model.UserManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private UserManager userManager = UserManager.shared();

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    private Stage mainStage;


    @FXML
    public void initialize() {
        if (errorLabel != null) {
            errorLabel.setText("");
            //observationManager = ObservationManager.getInstance();
        }
    }

    @FXML public void setMainStage(Stage mainStage) { this.mainStage = mainStage; }

    @FXML
    private void onSignIn() {
        if (emailField == null || passwordField == null || errorLabel == null) {
            throw new IllegalStateException("Login screen controls are not initialized");
        }
        boolean authenticated = userManager.login(emailField.getText(), passwordField.getText());
        passwordField.clear();
        if (authenticated) {
            errorLabel.setText("");
        } else {
            errorLabel.setText("Email or password is incorrect.");
        }
        //observationManager.setUser(user);


    }

    @FXML
    private void onCreateAccount() {
        if (errorLabel != null) {
            errorLabel.setText("Open the create-account screen to register.");
        }
    }
}
