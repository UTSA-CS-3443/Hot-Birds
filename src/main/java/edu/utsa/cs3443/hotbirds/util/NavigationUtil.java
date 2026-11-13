package edu.utsa.cs3443.hotbirds.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * All screen changes and popups go through the two public methods here:
 *   switchScreen - replaces what a window is showing (dashboard -> my observations)
 *   showPopup    - opens a modal popup on top of a window (add observation)
 */
public class NavigationUtil {
    // Absolute path, so this class can live in any package
    private static final String LAYOUTS = "/edu/utsa/cs3443/hotbirds/layouts/";

    // Implemented by controllers that need to know which Stage their screen is shown in
    public interface StageAware {
        void setMainStage(Stage stage);
    }

    private NavigationUtil() { }

    /**
     * Switches the given stage to another screen, e.g. switchScreen(stage, "my-observations.fxml").
     * Returns the new screen's controller (so the caller can pass it data), or null if loading failed.
     */
    public static <T> T switchScreen(Stage stage, String fxmlName) {
        try {
            FXMLLoader loader = new FXMLLoader(NavigationUtil.class.getResource(LAYOUTS + fxmlName));
            Parent root = loader.load();
            T controller = loader.getController();
            if (controller instanceof StageAware aware) {
                aware.setMainStage(stage);
            }

            Scene oldScene = stage.getScene();
            Scene scene = new Scene(root);
            if (oldScene != null) {
                scene.setFill(oldScene.getFill()); // keeps a transparent popup transparent
            }
            stage.setScene(scene);

            if (stage.getOwner() != null) { // a popup: resize to the new screen and re-center it
                stage.sizeToScene();
                centerOnOwner(stage);
            }
            return controller;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Opens a popup centered over the owner window, e.g. showPopup(stage, "add-observation.fxml").
     * Waits until the popup is closed, then returns its controller so the caller can read results
     * from it. Returns null if loading failed.
     */
    public static <T> T showPopup(Window owner, String fxmlName) {
        return showPopup(owner, fxmlName, null);
    }

    /**
     * Same as showPopup above, but runs setUp on the popup's controller before the popup
     * appears. Use it to hand the popup its data, e.g. the row being edited, since the
     * popup blocks everything until it closes.
     */
    public static <T> T showPopup(Window owner, String fxmlName, Consumer<Object> setUp) {
        try {
            FXMLLoader loader = new FXMLLoader(NavigationUtil.class.getResource(LAYOUTS + fxmlName));
            Parent root = loader.load();

            Stage dialog = new Stage();
            dialog.initOwner(owner);
            dialog.initModality(Modality.APPLICATION_MODAL); // blocks the main window until closed
            dialog.initStyle(StageStyle.TRANSPARENT);        // no title bar, allows rounded corners

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            dialog.setScene(scene);

            T controller = loader.getController();
            if (controller instanceof StageAware aware) {
                aware.setMainStage(dialog); // lets the popup close itself or switch its own screen
            }
            if (setUp != null && controller != null) {
                setUp.accept(controller); // fill the popup in before it is shown
            }

            dialog.setOnShown(e -> centerOnOwner(dialog)); // size is only known once shown
            dialog.showAndWait();
            return controller;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void centerOnOwner(Stage stage) {
        Window owner = stage.getOwner();
        if (owner == null) {
            return;
        }
        stage.setX(owner.getX() + (owner.getWidth() - stage.getWidth()) / 2);
        stage.setY(owner.getY() + (owner.getHeight() - stage.getHeight()) / 2);
    }
}
