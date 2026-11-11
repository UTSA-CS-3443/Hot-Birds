package edu.utsa.cs3443.hotbirds;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;

public class HomeScreenController {

    @FXML
    private Label contributions;
    //Total amount of observations the user has contributed

    @FXML
    private Label totalCount;
    //Total amount of observations completed by the community

    private Stage mainStage;
    NavigationUtil navigator = new NavigationUtil();

    @FXML
    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        navigator.logout(mainStage);
    }

    //Switch to my-observations screen
    @FXML
    private void handleObservations(MouseEvent event) {
        navigator.myObservations(mainStage);
    }

    @FXML
    private void handleUser(ActionEvent event) {
        navigator.userProfile(mainStage);
    }

}
