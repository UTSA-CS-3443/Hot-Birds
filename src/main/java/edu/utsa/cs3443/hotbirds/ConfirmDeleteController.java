package edu.utsa.cs3443.hotbirds;

import edu.utsa.cs3443.hotbirds.util.NavigationUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class ConfirmDeleteController implements NavigationUtil.StageAware {
    private Stage mainStage;
    private boolean confirmed;

    @FXML
    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    // True only if the user clicked "Yes, delete"; read by the screen that opened this popup
    public boolean isConfirmed() {
        return confirmed;
    }

    @FXML
    void handleCancel(ActionEvent event) {
        mainStage.close();
    }

    @FXML
    void handleNext(ActionEvent event) {
        confirmed = true;
        mainStage.close();
    }

}
