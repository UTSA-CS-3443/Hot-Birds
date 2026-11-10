package edu.utsa.cs3443.hotbirds;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeScreenController {

    @FXML
    private Label contributions;
    //Total amount of observations the user has contributed

    @FXML
    private Label totalCount;
    //Total amount of observations completed by the community

    private Stage mainStage;

    @FXML
    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    //Logout - Switch to login-screen
    @FXML
    void handleLogout(ActionEvent event){
        //observationManager.setCurrentUser(null);
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

    //Switch to my-observations screen
    @FXML
    void handleObservations(MouseEvent event) {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("layouts/my-observations.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            MyObservationsController observationsController = fxmlLoader.getController();
            observationsController.setMainStage(mainStage);

            mainStage.setScene(scene);

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    //Switch to profile-screen
    @FXML
    void handleUser(ActionEvent event) {

        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("layouts/profile-screen.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            ProfileController profileController = fxmlLoader.getController();
            profileController.setMainStage(mainStage);

            mainStage.setScene(scene);

        } catch (IOException e){
            e.printStackTrace();
        }

    }
}
