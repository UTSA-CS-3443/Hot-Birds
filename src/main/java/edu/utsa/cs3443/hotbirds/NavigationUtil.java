package edu.utsa.cs3443.hotbirds;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;


//TO DO: Figure out how to put this into a util folder ?
//I couldn't get the screen flows to work when it wasn't in the same folder as the controllers

//Put screen changing logic in here
//Then refer to this class when trying to switch screens, in order to reduce redundant code
public class NavigationUtil {

    //Logout - Switch to login-screen
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

    //Switch to profile-screen
    public void userProfile(Stage stage) {

        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("layouts/profile-screen.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            //Get the controller and set the stage
            ProfileController profileController = fxmlLoader.getController();
            profileController.setMainStage(stage);

            stage.setScene(scene);

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    //Switch to my-observations screen
    public void myObservations(Stage stage) {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("layouts/my-observations.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            MyObservationsController observationsController = fxmlLoader.getController();
            observationsController.setMainStage(stage);

            stage.setScene(scene);

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    //Switch to the home screen / dashboard
    public void homeScreen(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("layouts/home-screen.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            HomeScreenController homeScreenController = fxmlLoader.getController();
            homeScreenController.setMainStage(stage);

            stage.setScene(scene);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
