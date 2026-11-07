package edu.utsa.cs3443.hotbirds.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class EditObservationController {
    //This class is only a skeleton right now, I'm still working on it -Makaila

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField locationField;

    @FXML
    private TextField quantityField;

    @FXML
    private ComboBox<?> speciesBox;

    @FXML
    private TextField timeField;

    @FXML
    void handleCancel(ActionEvent event) {

    }

    @FXML
    void handleNext(ActionEvent event) {

    }

}
