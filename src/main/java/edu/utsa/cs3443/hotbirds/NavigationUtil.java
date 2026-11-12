package edu.utsa.cs3443.hotbirds;

import edu.utsa.cs3443.hotbirds.model.Observation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

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

    //Switch to Confirmation Observation screen
    public void confirmationObservation(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("layouts/confirm-observation.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            ConfirmObservationController observationController = fxmlLoader.getController();
            observationController.setMainStage(stage);

            stage.setScene(scene);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Switch to Confirm Delete Observation screen
    public void confirmDeleteObservation(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("layouts/confirm-delete.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            ConfirmDeleteController deleteController = fxmlLoader.getController();
            deleteController.setMainStage(stage);

            stage.setScene(scene);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Switch to Edit Observation screen
    public void editObservation(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("layouts/edit-observation.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            EditObservationController editController = fxmlLoader.getController();
            editController.setMainStage(stage);

            stage.setScene(scene);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addObservation(Stage stage, ListView<Observation> listView) {
        try {
            // Create a popup window that sits on top of the main window and blocks it until closed
            Stage dialog = new Stage();
            dialog.initOwner(listView.getScene().getWindow());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initStyle(StageStyle.TRANSPARENT); // No title bar and allows for rounded corners

            // Load the popup layout and make its background transparent
            FXMLLoader loader = new FXMLLoader(HotBirdsApplication.class.getResource("layouts/add-observation.fxml"));
            Scene scene = new Scene(loader.load());
            scene.setFill(Color.TRANSPARENT);

            // Giving Stylesheet to the popup
            scene.getStylesheets().add(HotBirdsApplication.class.getResource("styles/theme.css").toExternalForm());
            dialog.setScene(scene);

            // Centers the popup over the main window
            Window owner = dialog.getOwner();
            dialog.setOnShown(e -> {
                dialog.setX(owner.getX() + (owner.getWidth() - dialog.getWidth()) / 2);
                dialog.setY(owner.getY() + (owner.getHeight() - dialog.getHeight()) / 2);
            });

            dialog.showAndWait(); // Blocks interaction until popup is closed
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    }


