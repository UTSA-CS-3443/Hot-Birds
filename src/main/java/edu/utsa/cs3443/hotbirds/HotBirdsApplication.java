package edu.utsa.cs3443.hotbirds;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HotBirdsApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        //Start at the Login screen here (True starting point)
        //----------------------------------------------------------------------------------------------------------//
        /*FXMLLoader fxmlLoader = new FXMLLoader(HotBirdsApplication.class.getResource("layouts/login-screen.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 1024, 768);

        LoginController loginController = fxmlLoader.getController();
        loginController.setMainStage(stage);*/
        //----------------------------------------------------------------------------------------------------------//


        //Start at the home screen - FOR TESTING
        //----------------------------------------------------------------------------------------------------------//
        FXMLLoader fxmlLoader = new FXMLLoader(HotBirdsApplication.class.getResource("layouts/home-screen.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 1024, 768);

        HomeScreenController homeScreenController = fxmlLoader.getController();
        homeScreenController.setMainStage(stage);
        //PASSES the current stage TO the HomeScreenController through the setMainStage method within HomeScreenController
        //----------------------------------------------------------------------------------------------------------//

        stage.setTitle("Hot Birds");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}
