package edu.utsa.cs3443.hotbirds;

import edu.utsa.cs3443.hotbirds.model.Observation;
import edu.utsa.cs3443.hotbirds.model.ObservationManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;

public class MyObservationsController {
    @FXML private ListView<Observation> listView;

    private final ObservationManager manager = new ObservationManager();
    private final ObservableList<Observation> observations = FXCollections.observableArrayList();
    private Stage mainStage;
    NavigationUtil navigator = new NavigationUtil();

    @FXML
    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    // When my-observations.fxml loads: loads saved data and sets up the list
    @FXML
    public void initialize() {
        // Read the CSV into the manager's ArrayList
        try {
            manager.loadObservations();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Copy the manager's data into the ObservableList that the ListView displays
        observations.setAll(manager.getObservationArrayList());
        listView.setItems(observations);

        // Tells the ListView how to build each row
        listView.setCellFactory(lv -> new ListCell<>() {
            private Node root; // The row's layout
            private ObservationRowController rowController; // Controller for the row
            // Runs once per cell: load the row layout a single time and reuse it
            {
                try {
                    FXMLLoader loader = new FXMLLoader(HotBirdsApplication.class.getResource("layouts/observation-row.fxml"));
                    root = loader.load();
                    rowController = loader.getController();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            // Called by JavaFX whenever this cell is shown, scrolled to, or its data changes
            @Override
            public void updateItem(Observation item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    // No data for this cell so show nothing
                    setGraphic(null);
                } else {
                    // Fill the row with this observation and connect its row Edit/Delete buttons to this controllers edit and delete methods (found below)
                    rowController.setData(item, MyObservationsController.this::edit,
                            MyObservationsController.this::delete);
                    setGraphic(root); // display the row
                }
            }
        });
    }

    // Runs when the New Observation button or nav item is clicked
    @FXML
    private void handleAdd() {
        navigator.addObservation(mainStage, listView);
    }

    @FXML
    public void edit(Observation observation) {
        // open edit ui, then manager.updateObservation(observation) to save
        listView.refresh();
    }

    @FXML
    private void delete(Observation observation) {
        try {
            if (manager.deleteObservation(observation)) { // removes it from the ArrayList and the file
                observations.remove(observation);         // removes it from the list on screen
            }
        } catch (IOException e) {
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

    //Switch to home screen
    @FXML
    void handleHomeScreen(MouseEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("layouts/home-screen.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            HomeScreenController homeScreenController = fxmlLoader.getController();
            homeScreenController.setMainStage(mainStage);

            mainStage.setScene(scene);

        } catch (IOException e){
            e.printStackTrace();
        }
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
}