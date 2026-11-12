package edu.utsa.cs3443.hotbirds;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.InputStream;

public class ConfirmObservationController {
    //This class is only a skeleton right now, I'm still working on it -Makaila
    private Stage mainStage;
    NavigationUtil navigator = new NavigationUtil();

    //Display the appropriate bird photo based on information that user entered
    @FXML
    private ImageView birdPhoto;

    @FXML
    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    @FXML
    public void setImageView(String birdType) {

        /* Set the image based on user input here
        // "Red" -> /images/red.png
        String path = "/images/" + birdType.toLowerCase() + ".png";
        InputStream stream = getClass().getResourceAsStream(path);

        if (stream == null) {
            System.out.println("Image not found: " + path);
        }
        birdPhoto.setImage(new Image(stream));*/
    }

    @FXML
    void handleCancel(ActionEvent event) {
        //TO DO: Edit this method so that when it goes back to the create observation screen,
        //It keeps all the information the user originally entered
        //navigator.addObservation();
    }

    @FXML
    void handleNext(ActionEvent event) {
        //If confirmed, Add the information to the observation data file
        //And add to the My Observations page display

    }

}
