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

    //private Observation result;

    @FXML
    public void initialize() {
        speciesBox.setItems(FXCollections.observableArrayList(
                "American Goldfinch", "Eastern Bluebird", "Painted Bunting",
                "Spotted Towhee", "Red-breasted Nuthatch", "Great Blue Heron",
                "Red-bellied Woodpecker"
        ));
    }

    @FXML
    private void handleCancel() {
        ((Stage) speciesBox.getScene().getWindow()).close();
    }

    @FXML
    private void handleNext() {
        // add confirm logic later
        //Upon next, switch to confirmation screen
    }
}
