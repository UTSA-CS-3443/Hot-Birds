package edu.utsa.cs3443.hotbirds;

import edu.utsa.cs3443.hotbirds.model.Observation;
import edu.utsa.cs3443.hotbirds.model.UserManager;
import edu.utsa.cs3443.hotbirds.util.NavigationUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;

public class AddObservationController implements NavigationUtil.StageAware {
    @FXML private ComboBox<String> speciesBox;
    @FXML private DatePicker datePicker;
    @FXML private TextField timeField, quantityField, locationField;
    @FXML private Label errorLabel;

    private Stage mainStage; // the popup window this screen is shown in

    // Filled in when the user clicks Next, so Go back can put the values back on the form
    private Observation pending;

    public void setMainStage(Stage mainStage) { this.mainStage = mainStage; }

    @FXML
    public void initialize() {
        speciesBox.setItems(FXCollections.observableArrayList(
                "American Goldfinch", "Eastern Bluebird", "Painted Bunting",
                "Spotted Towhee", "Red-breasted Nuthatch"
        ));
        showError("");
    }

    // Puts an already-entered observation back on the form (used by the confirm screen's Go back)
    public void setObservation(Observation observation) {
        if (observation == null) {
            return;
        }
        pending = observation;
        speciesBox.setValue(observation.getSpecies());
        quantityField.setText(Integer.toString(observation.getCount()));
        timeField.setText((observation.getTime() + " " + observation.getAmPm()).trim());
        locationField.setText(observation.getLocation());
        datePicker.setValue(ObservationForm.parseDate(observation.getDate()));
    }

    @FXML
    private void handleCancel() {
        mainStage.close();
    }

    @FXML
    private void handleNext() {
        // Turn what's on the form into an Observation; null means something was missing
        Observation entered = ObservationForm.build(
                UserManager.shared().getActiveUser(),
                speciesBox.getValue(), quantityField.getText(),
                datePicker.getValue(), timeField.getText(), locationField.getText());

        if (entered == null) {
            showError("Please fill in every field (quantity must be a number, time like 6:40 AM).");
            return;
        }
        pending = entered;

        // Show the confirm screen inside this same popup, and hand it the observation
        ConfirmObservationController confirm =
                NavigationUtil.switchScreen(mainStage, "confirm-observation.fxml");
        if (confirm != null) {
            confirm.setObservation(pending);
        }
    }

    private void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
        } else if (!message.isEmpty()) {
            System.out.println(message);
        }
    }
}
