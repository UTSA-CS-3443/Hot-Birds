package edu.utsa.cs3443.hotbirds;

import edu.utsa.cs3443.hotbirds.model.User;
import edu.utsa.cs3443.hotbirds.model.UserManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class ProfileController {
    @FXML
    private Label usernameLabel;

    @FXML
    private Label fullNameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Stage mainStage;

    private UserManager userManager = UserManager.shared();

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    //Sets profile-screen to the stage
    @FXML
    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    @FXML
    public void initialize() {
        User user = userManager.findUser(userManager.getActiveUser());
        if (user == null) {
            // The profile can be opened without an active session; keep the labels empty in that case.
            setProfileValues("", "", "");
            return;
        }

        // Accounts without a saved full name display their username instead.
        String name = user.getName().isBlank() ? user.getUsername() : user.getName();
        setProfileValues(user.getUsername(), name, user.getEmail());
    }

    @FXML
    private void onLogOut(ActionEvent event) {
        // Clear the session before navigating away from the profile.
        userManager.logout();
        setProfileValues("", "", "");

        // Load the login screen and pass it the existing stage so navigation reuses the window.
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("layouts/login-screen.fxml"));
            Scene loginScene = new Scene(fxmlLoader.load());

            // Get the controller and set the stage
            LoginController loginController = fxmlLoader.getController();
            loginController.setMainStage(mainStage);

            mainStage.setScene(loginScene);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onReturnToDashboard(ActionEvent event) {
        if (userManager.getActiveUser() == null) {
            setProfileValues("", "", "");
        }

        // Return to the dashboard in the same window.
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("layouts/home-screen.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            HomeScreenController homeScreenController = fxmlLoader.getController();
            homeScreenController.setMainStage(mainStage);

            mainStage.setScene(scene);

        } catch (IOException e) {
            e.printStackTrace();
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
