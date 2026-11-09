package edu.utsa.cs3443.hotbirds;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HotBirdsApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        //FXMLLoader fxmlLoader = new FXMLLoader(HotBirdsApplication.class.getResource("layouts/my-observations.fxml"));
        //FXMLLoader fxmlLoader = new FXMLLoader(HotBirdsApplication.class.getResource("layouts/login-screen.fxml"));

        FXMLLoader fxmlLoader = new FXMLLoader(HotBirdsApplication.class.getResource("layouts/home-screen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1024, 768);

        HomeScreenController homeScreenController = fxmlLoader.getController();
        homeScreenController.setMainStage(stage);

        stage.setTitle("Hot Birds");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}
