package edu.utsa.cs3443.hotbirds.controllers;

import edu.utsa.cs3443.hotbirds.model.User;
import edu.utsa.cs3443.hotbirds.model.UserManager;
import edu.utsa.cs3443.hotbirds.util.NavigationUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Window;
import javafx.stage.Stage;

public class ProfileController implements NavigationUtil.StageAware {
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
        userManager.logout();
        setProfileValues("", "", "");
        Window owner = mainStage.getOwner(); // Gets window that opened the profile popup.
        if (owner instanceof Stage ownerStage) {
            mainStage.close(); // Closes the profile popup.

            // Changes either dashboard or observation list page to login page.
            NavigationUtil.switchScreen(ownerStage, "login-screen.fxml");
        } else {
            // if the owner stage isn't there it opens on the current page.
            NavigationUtil.switchScreen(mainStage, "login-screen.fxml");
        }
    }

    @FXML
    private void onReturnToDashboard(ActionEvent event) {
        mainStage.close();
    }

    private void setProfileValues(String username, String fullName, String email) {
        if (usernameLabel != null) usernameLabel.setText(username);
        if (fullNameLabel != null) fullNameLabel.setText(fullName);
        if (emailLabel != null) emailLabel.setText(email);
    }

}
