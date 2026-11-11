package edu.utsa.cs3443.hotbirds;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


//TO DO: Figure out how to put this into a util folder ?
//I couldn't get the screen flows to work when it wasn't in the same folder as the controllers

//Put screen changing logic in here
//Then refer to this class when trying to switch screens, in order to reduce redundant code
public class NavigationUtil {

    private Stage mainStage;

    //Logout - Switch to login-screen
    @FXML
    public void logout(Stage stage){
        //observationManager.setCurrentUser(null);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("layouts/login-screen.fxml"));
            Scene loginScene = new Scene(fxmlLoader.load());

            // Get the controller and set the stage
            LoginController loginController = fxmlLoader.getController();
            loginController.setMainStage(stage);

            stage.setScene(loginScene);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
