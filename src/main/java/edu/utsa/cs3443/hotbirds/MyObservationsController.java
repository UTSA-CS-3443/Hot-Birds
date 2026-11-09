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

    @FXML
    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    @FXML
    public void initialize() {
        try {
            manager.loadObservations();
        } catch (IOException e) {
            e.printStackTrace();
        }
        observations.setAll(manager.getObservationArrayList());

        listView.setItems(observations);
        listView.setCellFactory(lv -> new ListCell<>() {
            private Node root;
            private ObservationRowController rowController;
            {
                try {
                    FXMLLoader loader = new FXMLLoader(HotBirdsApplication.class.getResource("layouts/observation-row.fxml"));
                    root = loader.load();
                    rowController = loader.getController();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void updateItem(Observation item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    rowController.setData(item, MyObservationsController.this::edit,
                            MyObservationsController.this::delete);
                    setGraphic(root);
                }
            }
        });
    }

    // opens add observation popup
    @FXML
    private void handleAdd() {
        try {
            Stage dialog = new Stage();
            dialog.initOwner(listView.getScene().getWindow());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initStyle(StageStyle.TRANSPARENT);

            FXMLLoader loader = new FXMLLoader(HotBirdsApplication.class.getResource("layouts/add-observation.fxml"));
            Scene scene = new Scene(loader.load());
            scene.setFill(Color.TRANSPARENT);
            scene.getStylesheets().add(HotBirdsApplication.class.getResource("styles/theme.css").toExternalForm());
            dialog.setScene(scene);
            Window owner = dialog.getOwner();
            dialog.setOnShown(e -> {
                dialog.setX(owner.getX() + (owner.getWidth() - dialog.getWidth()) / 2);
                dialog.setY(owner.getY() + (owner.getHeight() - dialog.getHeight()) / 2);
            });
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void edit(Observation observation) {
        // open edit ui, then manager.updateObservation(observation) to save
        listView.refresh();
    }

    private void delete(Observation observation) {
        try {
            if (manager.deleteObservation(observation)) { // removes it from the ArrayList and the file
                observations.remove(observation);         // removes it from the list on screen
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Show user profile when User button is clicked
    @FXML
    void handleUser(ActionEvent event) {
        ProfileController profileController = new ProfileController();
        profileController.handleUser();
    }
}