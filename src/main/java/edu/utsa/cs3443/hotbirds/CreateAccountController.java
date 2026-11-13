package edu.utsa.cs3443.hotbirds;

import edu.utsa.cs3443.hotbirds.model.User;
import edu.utsa.cs3443.hotbirds.model.UserManager;
import edu.utsa.cs3443.hotbirds.util.NavigationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class CreateAccountController implements NavigationUtil.StageAware {
    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

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

        User user = new User(firstNameField.getText(), lastNameField.getText(),
                usernameField.getText(), passwordField.getText(), emailField.getText());
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
        showLoginScreen("Account created. You can now sign in.");
    }

    @FXML
    private void onCancel() {
        showLoginScreen("");
    }

    // Returns to the login screen and shows a message there (e.g. the welcome message)
    private void showLoginScreen(String statusMessage) {
        LoginController loginController = NavigationUtil.switchScreen(mainStage, "login-screen.fxml");
        if (loginController != null) {
            loginController.setUserManager(userManager);
            loginController.setStatusMessage(statusMessage);
        } else {
            errorLabel.setText("Unable to return to sign in.");
        }
    }

    private boolean isInputValid() {
        if (firstNameField == null || lastNameField == null || usernameField == null
                || emailField == null || passwordField == null
                || confirmPasswordField == null || errorLabel == null) {
            throw new IllegalStateException("Create-account screen controls are not initialized");
        }
        // Validate required values before checking format and matching passwords.
        if (firstNameField.getText().isBlank() || lastNameField.getText().isBlank()
                || usernameField.getText().isBlank() || emailField.getText().isBlank()
                || passwordField.getText().isBlank()) {
            errorLabel.setText("Name, username, email, and password are required.");
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
