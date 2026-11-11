package edu.utsa.cs3443.hotbirds;

import edu.utsa.cs3443.hotbirds.model.User;
import edu.utsa.cs3443.hotbirds.model.UserManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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

    private UserManager userManager = UserManager.shared();

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
            if (userManager.createUser(user)) {
                errorLabel.setText("Account created. You can now sign in.");
                // Remove credentials from the form after successful account creation.
                passwordField.clear();
                confirmPasswordField.clear();
            } else {
                errorLabel.setText("That username or email is already in use.");
            }
        } catch (IOException exception) {
            errorLabel.setText("Unable to save the account: " + exception.getMessage());
        }
    }

    @FXML
    private void onCancel() {
        if (errorLabel != null) {
            errorLabel.setText("");
        }
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
