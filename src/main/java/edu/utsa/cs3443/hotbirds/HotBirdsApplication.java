package edu.utsa.cs3443.hotbirds;

import edu.utsa.cs3443.hotbirds.controllers.HomeScreenController;
import edu.utsa.cs3443.hotbirds.controllers.LoginController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

//TO DO: Check to make sure the screens are coming out at consistent sizes if their sizes are static
public class HotBirdsApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        //Start at the Login screen (True starting point)
        //The screens show the signed-in user's name and records, so the app has to start here
        //----------------------------------------------------------------------------------------------------------//
        FXMLLoader fxmlLoader = new FXMLLoader(HotBirdsApplication.class.getResource("layouts/login-screen.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 1024, 768);

        LoginController loginController = fxmlLoader.getController();
        loginController.setMainStage(stage);
        //PASSES the current stage TO the controller through its setMainStage method
        //----------------------------------------------------------------------------------------------------------//


        //Start at the home screen - FOR TESTING (no user is signed in, so counts show 0)
        //----------------------------------------------------------------------------------------------------------//
        /*FXMLLoader fxmlLoader = new FXMLLoader(HotBirdsApplication.class.getResource("layouts/home-screen.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 1024, 768);

        HomeScreenController homeScreenController = fxmlLoader.getController();
        homeScreenController.setMainStage(stage);*/
        //----------------------------------------------------------------------------------------------------------//

        // Adds App Icon
        for (int size : new int[] {256, 128, 64, 48, 32, 16}) {
            stage.getIcons().add(new Image(HotBirdsApplication.class
                    .getResourceAsStream("images/icons/app-icon-" + size + ".png")));
        }

        stage.setTitle("Hot Birds");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}
