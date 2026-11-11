package edu.utsa.cs3443.hotbirds;

import edu.utsa.cs3443.hotbirds.model.User;
import edu.utsa.cs3443.hotbirds.model.UserManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
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
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("layouts/home-screen.fxml"));
                Scene scene = new Scene(fxmlLoader.load());

                HomeScreenController homeScreenController = fxmlLoader.getController();
                homeScreenController.setMainStage(mainStage);

                mainStage.setScene(scene);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            errorLabel.setText("Email or password is incorrect.");
        }
    }

    @FXML
    private void onCreateAccount() {
        if (errorLabel != null) {
            errorLabel.setText("Open the create-account screen to register.");
        }
    }
}
