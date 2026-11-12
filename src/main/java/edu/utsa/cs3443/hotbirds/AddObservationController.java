package edu.utsa.cs3443.hotbirds;

import edu.utsa.cs3443.hotbirds.model.Observation;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddObservationController {
    @FXML private ComboBox<String> speciesBox;
    @FXML private DatePicker datePicker;
    @FXML private TextField timeField, quantityField, locationField;
    NavigationUtil navigator = new NavigationUtil();
    private Stage mainStage;

    //private Observation result;
    public void setMainStage(Stage mainStage) { this.mainStage = mainStage; }

    @FXML
    public void initialize() {
        speciesBox.setItems(FXCollections.observableArrayList(
                "American Goldfinch", "Eastern Bluebird", "Painted Bunting",
                "Spotted Towhee", "Red-breasted Nuthatch", "Great Blue Heron",
                "Red-bellied Woodpecker"
        ));
    }

    //Save user data into variables when entered for us to go back to if they cancel
    //Or to save to file if they confirm

    @FXML
    private void handleCancel() {
        ((Stage) speciesBox.getScene().getWindow()).close();
    }

    @FXML
    private void handleNext() {
        //Proceed to confirm screen before adding to list of observations
        //Pass speciesBox variable so confirmation screen knows which species to display
        navigator.confirmObservation(mainStage);
    }
}
