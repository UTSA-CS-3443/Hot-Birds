package edu.utsa.cs3443.hotbirds.controllers;

import edu.utsa.cs3443.hotbirds.model.Observation;
import edu.utsa.cs3443.hotbirds.model.ObservationManager;
import edu.utsa.cs3443.hotbirds.util.NavigationUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;

public class ConfirmObservationController implements NavigationUtil.StageAware {
    private static final String BIRD_IMAGES = "/edu/utsa/cs3443/hotbirds/images/birds/";

    private Stage mainStage;

    //Display the appropriate bird photo based on information that user entered
    @FXML private ImageView birdPhoto;
    @FXML private Label summaryLabel;

    private Observation pending;  // the observation waiting to be saved
    private boolean saved;        // true once the user confirmed and it was written

    @FXML
    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    // Called by the Add screen with the observation the user just entered
    public void setObservation(Observation observation) {
        this.pending = observation;
        if (observation == null) {
            return;
        }
        setImageView(observation.getSpecies());
        if (summaryLabel != null) {
            summaryLabel.setText(observation.getCount() + " × " + observation.getSpecies()
                    + " at " + observation.getLocation() + " on " + observation.getDisplayDate());
        }
    }

    // True only if the user confirmed; the My Observations screen checks this to refresh
    public boolean isSaved() { return saved; }

    public Observation getObservation() { return pending; }

    @FXML
    public void setImageView(String birdType) {
        if (birdPhoto == null || birdType == null) {
            return;
        }
        // Each species has a photo named after it, e.g. "American Goldfinch.png"
        String path = BIRD_IMAGES + birdType + ".png";
        InputStream stream = getClass().getResourceAsStream(path);
        if (stream == null) {
            System.out.println("Image not found: " + path);
            birdPhoto.setImage(null);
            return;
        }
        birdPhoto.setImage(new Image(stream));
    }

    @FXML
    void handleCancel(ActionEvent event) {
        // Back to the add screen, with everything the user typed still filled in
        AddObservationController add = NavigationUtil.switchScreen(mainStage, "add-observation.fxml");
        if (add != null) {
            add.setObservation(pending);
        }
    }

    @FXML
    void handleNext(ActionEvent event) {
        // Confirmed: add it to the shared list and write the CSV, then close the popup
        try {
            if (pending != null) {
                saved = ObservationManager.shared().saveNewObservation(pending);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        mainStage.close();
    }
}
