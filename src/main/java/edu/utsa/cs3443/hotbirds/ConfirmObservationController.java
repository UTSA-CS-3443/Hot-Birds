package edu.utsa.cs3443.hotbirds;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class ConfirmObservationController {
    //This class is only a skeleton right now, I'm still working on it -Makaila
    private Stage mainStage;

    @FXML
    private ImageView birdPhoto;

    @FXML
    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    @FXML
    void handleCancel(ActionEvent event) {

    }

    @FXML
    void handleNext(ActionEvent event) {

    }

}
