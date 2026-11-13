package edu.utsa.cs3443.hotbirds;

import edu.utsa.cs3443.hotbirds.model.Observation;
import edu.utsa.cs3443.hotbirds.model.ObservationManager;
import edu.utsa.cs3443.hotbirds.util.NavigationUtil;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class EditObservationController implements NavigationUtil.StageAware {

    private Stage mainStage;

    @FXML private DatePicker datePicker;
    @FXML private TextField locationField;
    @FXML private TextField quantityField;
    @FXML private ComboBox<String> speciesBox;
    @FXML private TextField timeField;
    @FXML private Label errorLabel; // optional: only shows a message if the FXML has it

    private Observation observation; // the row being edited
    private boolean changed;         // true if the edit was saved
    private boolean deleted;         // true if the user deleted it from here

    @FXML
    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    // Loads list of different birds to pick from
    @FXML
    public void initialize() {
        speciesBox.setItems(FXCollections.observableArrayList(
                "American Goldfinch", "Eastern Bluebird", "Painted Bunting",
                "Spotted Towhee", "Red-breasted Nuthatch"
        ));
        showError("");
    }

    // Called by the list with the row that was clicked; fills the form with its current values
    public void setObservation(Observation observation) {
        this.observation = observation;
        if (observation == null) {
            return;
        }
        speciesBox.setValue(observation.getSpecies());
        quantityField.setText(Integer.toString(observation.getCount()));
        timeField.setText((observation.getTime() + " " + observation.getAmPm()).trim());
        locationField.setText(observation.getLocation());
        datePicker.setValue(ObservationForm.parseDate(observation.getDate()));
    }

    public boolean isChanged() { return changed; }
    public boolean isDeleted() { return deleted; }

    @FXML
    void handleCancel(ActionEvent event) {
        mainStage.close();
    }

    @FXML
    void handleNext(ActionEvent event) {
        if (observation == null) {
            mainStage.close();
            return;
        }
        // Read the form the same way the Add screen does, so the checks match
        Observation edited = ObservationForm.build(observation.getObserver(),
                speciesBox.getValue(), quantityField.getText(),
                datePicker.getValue(), timeField.getText(), locationField.getText());

        if (edited == null) {
            showError("Please fill in every field (quantity must be a number, time like 6:40 AM).");
            return;
        }

        // Copy the new values onto the row the list already holds, then save the file
        observation.setSpecies(edited.getSpecies());
        observation.setCount(edited.getCount());
        observation.setTime(edited.getTime());
        observation.setAmPm(edited.getAmPm());
        observation.setDate(edited.getDate());
        observation.setLocation(edited.getLocation());
        observation.setAddress(edited.getAddress());

        try {
            ObservationManager.shared().updateObservation(observation);
            changed = true;
        } catch (IOException e) {
            e.printStackTrace();
            showError("Couldn't save the changes.");
            return;
        }
        mainStage.close();
    }

    // The Delete button on this screen: asks first, then removes the observation
    @FXML
    void handleDelete(ActionEvent event) {
        if (observation == null) {
            mainStage.close();
            return;
        }
        ConfirmDeleteController confirm = NavigationUtil.showPopup(mainStage, "confirm-delete.fxml");
        if (confirm == null || !confirm.isConfirmed()) {
            return;
        }
        try {
            deleted = ObservationManager.shared().deleteObservation(observation);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mainStage.close();
    }

    private void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
        } else if (!message.isEmpty()) {
            System.out.println(message);
        }
    }
}
