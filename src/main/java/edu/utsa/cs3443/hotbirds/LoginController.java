package edu.utsa.cs3443.hotbirds;
//test comment 11/11/26 - zaid
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
    NavigationUtil navigator = new NavigationUtil();


    @FXML
    public void initialize() {
        if (errorLabel != null) {
            errorLabel.setText("");
            //observationManager = ObservationManager.getInstance(); <-Just an idea for future use, not sure if I want to use
        }
    }

    @FXML public void setMainStage(Stage mainStage) { this.mainStage = mainStage; }

    @FXML
    private void onSignIn() {
        if (emailField == null || passwordField == null || errorLabel == null) {
            throw new IllegalStateException("Login screen controls are not initialized");
        }
        // Clear the password immediately after authentication, whether the credentials match or not.
        boolean authenticated = userManager.login(emailField.getText(), passwordField.getText());
        passwordField.clear();
        if (authenticated) {
            navigator.homeScreen(mainStage);
        } else {
            errorLabel.setText("Email or password is incorrect.");
        }
    }

    @FXML
    private void onCreateAccount() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("layouts/create-account-screen.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            CreateAccountController createAccountController = fxmlLoader.getController();
            createAccountController.setMainStage(mainStage);
            createAccountController.setUserManager(userManager);

            mainStage.setScene(scene);
        } catch (IOException e) {
            if (errorLabel != null) {
                errorLabel.setText("Unable to open the create-account screen: " + e.getMessage());
            }
        }
    }

    public void setStatusMessage(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
        }
    }
}
