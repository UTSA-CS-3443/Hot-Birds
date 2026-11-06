package edu.utsa.cs3443.hotbirds.controllers;

import edu.utsa.cs3443.hotbirds.model.User;
import edu.utsa.cs3443.hotbirds.model.UserManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ProfileController {
    @FXML
    private Label usernameLabel;

    @FXML
    private Label fullNameLabel;

    @FXML
    private Label emailLabel;

    private UserManager userManager = UserManager.shared();

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    @FXML
    public void initialize() {
        User user = userManager.findUser(userManager.getActiveUser());
        if (user == null) {
            setProfileValues("", "", "");
            return;
        }

        String name = user.getName().isBlank() ? user.getUsername() : user.getName();
        setProfileValues(user.getUsername(), name, user.getEmail());
    }

    @FXML
    private void onLogOut() {
        userManager.logout();
        setProfileValues("", "", "");
    }

    @FXML
    private void onReturnToDashboard() {
        if (userManager.getActiveUser() == null) {
            setProfileValues("", "", "");
        }
    }

    private void setProfileValues(String username, String fullName, String email) {
        if (usernameLabel != null) {
            usernameLabel.setText(username);
        }
        if (fullNameLabel != null) {
            fullNameLabel.setText(fullName);
        }
        if (emailLabel != null) {
            emailLabel.setText(email);
        }
    }
}
