package edu.utsa.cs3443.hotbirds;

import edu.utsa.cs3443.hotbirds.model.User;
import edu.utsa.cs3443.hotbirds.model.UserManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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

    //Sets profile-screen to the stage
    @FXML
    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
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

    //TO DO: Fix this method so it works for any of the screens as long as they create an instance of ProfileController
    //Whenever the User button is clicked on any of the pages, call upon this method to switch to Profile-screen
    @FXML
    void handleUser() {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("layouts/profile-screen.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            setMainStage(mainStage);
            mainStage.setScene(scene);
            mainStage.show();

        } catch (IOException e){
            e.printStackTrace();
        }

    }
}
