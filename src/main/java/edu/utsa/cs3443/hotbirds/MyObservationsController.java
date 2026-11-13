package edu.utsa.cs3443.hotbirds;

import edu.utsa.cs3443.hotbirds.model.Observation;
import edu.utsa.cs3443.hotbirds.model.ObservationManager;
import edu.utsa.cs3443.hotbirds.model.User;
import edu.utsa.cs3443.hotbirds.model.UserManager;
import edu.utsa.cs3443.hotbirds.util.NavigationUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class MyObservationsController implements NavigationUtil.StageAware {
    @FXML private ListView<Observation> listView;
    @FXML private Label logLabel;      // "MAYA'S LOG" above the page title
    @FXML private Label recordsLabel;  // "12 records" under the card heading
    @FXML private Label footerLabel;   // "Showing 12 of 46 records" at the bottom
    @FXML private Button profileButton; // the "Name" button in the header

    // The one manager the whole app shares, so adds and deletes show up everywhere
    private final ObservationManager manager = ObservationManager.shared();
    private final ObservableList<Observation> observations = FXCollections.observableArrayList();
    private Stage mainStage;

    @FXML
    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    // When my-observations.fxml loads: loads saved data and sets up the list
    @FXML
    public void initialize() {
        // Read the CSV into the manager's ArrayList
        try {
            manager.loadObservations();
        } catch (IOException e) {
            e.printStackTrace();
        }
        refreshList(); // fills the list with this user's rows and updates the counts

        listView.setItems(observations);

        // Tells the ListView how to build each row
        listView.setCellFactory(lv -> new ListCell<>() {
            private Node root; // The row's layout
            private ObservationRowController rowController; // Controller for the row
            // Runs once per cell: load the row layout a single time and reuse it
            {
                try {
                    FXMLLoader loader = new FXMLLoader(HotBirdsApplication.class.getResource("layouts/observation-row.fxml"));
                    root = loader.load();
                    rowController = loader.getController();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            // Called by JavaFX whenever this cell is shown, scrolled to, or its data changes
            @Override
            public void updateItem(Observation item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    // No data for this cell so show nothing
                    setGraphic(null);
                } else {
                    // Fill the row with this observation and connect its row Edit/Delete buttons to this controllers edit and delete methods (found below)
                    rowController.setData(item, MyObservationsController.this::edit,
                            MyObservationsController.this::delete);
                    setGraphic(root); // display the row
                }
            }
        });
    }


    //Shows only the signed-in user's observations and updates the three count labels.
    //Called at startup and again after anything is added, edited or deleted.
    private void refreshList() {
        String username = UserManager.shared().getActiveUser();

        observations.setAll(manager.getObservationsFor(username));

        int shown = observations.size();              // this user's records
        int total = manager.getTotalRecordCount();    // every record in the file

        if (logLabel != null) {
            logLabel.setText(displayName(username).toUpperCase() + "'S LOG");
        }
        if (profileButton != null) {
            profileButton.setText(displayName(username));
        }
        if (recordsLabel != null) {
            recordsLabel.setText(shown + (shown == 1 ? " record" : " records"));
        }
        if (footerLabel != null) {
            footerLabel.setText("Showing " + shown + " of " + total + " records");
        }
    }

    // The user's first name if we have it
    private String displayName(String username) {
        if (username == null || username.isBlank()) {
            return "Guest";
        }
        User user = UserManager.shared().findUser(username);
        if (user == null || user.getFirstName().isBlank()) {
            return username;
        }
        return user.getFirstName();
    }

    // Runs when the New Observation button or nav item is clicked
    @FXML
    private void handleAdd() {
        NavigationUtil.showPopup(mainStage, "add-observation.fxml");
        refreshList(); // the popup may have saved a new observation
    }

    @FXML
    public void edit(Observation observation) {
        // Open the edit popup with this row's values already filled in
        // The lambda runs on the popup's controller after it's built but before it opens,
        // which lets it be sent to that row
        EditObservationController editor = NavigationUtil.showPopup(mainStage, "edit-observation.fxml", c -> ((EditObservationController) c).setObservation(observation));
        if (editor != null && (editor.isChanged() || editor.isDeleted())) {
            refreshList(); // saved or deleted from inside the popup
        }
        listView.refresh();
    }

    @FXML
    private void delete(Observation observation) {
        // Asks for confirmation, the popup's controller remembers whether the user confirmed
        ConfirmDeleteController confirm = NavigationUtil.showPopup(mainStage, "confirm-delete.fxml");
        if (confirm == null || !confirm.isConfirmed()) {
            return;
        }
        try {
            if (manager.deleteObservation(observation)) { // removes it from the ArrayList and the file
                refreshList();                            // redraws the list and the counts
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Switch to profile-screen
    @FXML
    void handleUser(ActionEvent event) {
        NavigationUtil.showPopup(mainStage, "profile-screen.fxml");
    }

    //Switch to home screen
    @FXML
    void handleHomeScreen(MouseEvent event) {
        NavigationUtil.switchScreen(mainStage, "home-screen.fxml");
    }

    //Logout - Switch to login-screen
    @FXML
    void handleLogout(ActionEvent event) {
        UserManager.shared().logout();
        NavigationUtil.switchScreen(mainStage, "login-screen.fxml");
    }
}
