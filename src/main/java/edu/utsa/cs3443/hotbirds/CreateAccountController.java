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

public class CreateAccountController {
    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label errorLabel;

    private Stage mainStage;
    private UserManager userManager = UserManager.shared();
    NavigationUtil navigator = new NavigationUtil();

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    @FXML
    public void initialize() {
        if (errorLabel != null) {
            errorLabel.setText("");
        }
    }

    @FXML
    private void onCreateAccount() {
        // Do not create or persist an account until all form fields pass validation.
        if (!isInputValid()) {
            return;
        }

        User user = new User(usernameField.getText(), emailField.getText(),
                passwordField.getText());
        try {
            if (!userManager.createUser(user)) {
                errorLabel.setText("That username or email is already in use.");
                return;
            }
        } catch (IOException exception) {
            errorLabel.setText("Unable to save the account: " + exception.getMessage());
            return;
        }

        // Remove credentials from the form after successful account creation.
        passwordField.clear();
        confirmPasswordField.clear();
        try {
            showLoginScreen("Account created. You can now sign in.");
        } catch (IOException exception) {
            errorLabel.setText("Account created, but unable to return to sign in: "
                    + exception.getMessage());
        }
    }

    @FXML
    private void onCancel() {
        try {
            showLoginScreen("");
        } catch (IOException exception) {
            errorLabel.setText("Unable to return to sign in: " + exception.getMessage());
        }
    }

    //TO DO: Is there some way we can move this screen logic to the Navigation util class? I don't want to mess up the welcome message
    private void showLoginScreen(String statusMessage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("layouts/login-screen.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        LoginController loginController = fxmlLoader.getController();
        loginController.setMainStage(mainStage);
        loginController.setUserManager(userManager);
        loginController.setStatusMessage(statusMessage);

        mainStage.setScene(scene);
    }

    private boolean isInputValid() {
        if (usernameField == null || emailField == null || passwordField == null
                || confirmPasswordField == null || errorLabel == null) {
            throw new IllegalStateException("Create-account screen controls are not initialized");
        }
        // Validate required values before checking format and matching passwords.
        if (usernameField.getText().isBlank() || emailField.getText().isBlank()
                || passwordField.getText().isBlank()) {
            errorLabel.setText("Username, email, and password are required.");
            return false;
        }
        if (!emailField.getText().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            errorLabel.setText("Enter a valid email address.");
            return false;
        }
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            errorLabel.setText("Passwords do not match.");
            return false;
        }
        return true;
    }
}
