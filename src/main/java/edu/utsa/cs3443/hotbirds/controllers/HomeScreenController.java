package edu.utsa.cs3443.hotbirds.controllers;

import edu.utsa.cs3443.hotbirds.util.NavigationUtil;
import edu.utsa.cs3443.hotbirds.model.ObservationManager;
import edu.utsa.cs3443.hotbirds.model.User;
import edu.utsa.cs3443.hotbirds.model.UserManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class HomeScreenController implements NavigationUtil.StageAware {

    // The counts stay the lighter green; the bars use the sidebar's dark green
    private static final String ACCENT = "#388e3c";
    private static final String BAR_COLOR = "#17352D";

    @FXML
    private Label contributions;
    //Total amount of observations the user has contributed

    @FXML
    private Label totalCount;
    //Total amount of observations completed by the community

    @FXML
    private Label totalCaption; //The small line under the community total

    @FXML
    private Button profileButton; //The "Name" button in the header

    // The five rows of the "Species to Watch For" card
    @FXML private VBox speciesRow1, speciesRow2, speciesRow3, speciesRow4, speciesRow5;
    @FXML private Label speciesName1, speciesName2, speciesName3, speciesName4, speciesName5;
    @FXML private Label speciesStat1, speciesStat2, speciesStat3, speciesStat4, speciesStat5;
    @FXML private ProgressBar speciesBar1, speciesBar2, speciesBar3, speciesBar4, speciesBar5;

    private Stage mainStage;

    @FXML
    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    // Runs when home-screen.fxml loads
    @FXML
    public void initialize() {
        refresh();
    }

    // Fills in every number on the screen from the observation data
    private void refresh() {
        ObservationManager manager = ObservationManager.shared();
        String username = UserManager.shared().getActiveUser();

        if (totalCount != null) {
            // Every bird everyone counted, with a comma for thousands (1,470)
            totalCount.setText(String.format("%,d", manager.getTotalBirdsCounted()));
        }
        if (totalCaption != null) {
            // The data only covers one year, so say what the number is made of instead
            int sightings = manager.getTotalRecordCount();
            totalCaption.setText("across " + sightings + (sightings == 1 ? " sighting" : " sightings"));
        }
        if (contributions != null) {
            // How many sightings this user logged
            contributions.setText(String.format("%,d", manager.getRecordCountFor(username)));
        }
        if (profileButton != null) {
            profileButton.setText(displayName(username));
        }

        fillSpeciesCard(manager);
    }


    //Fills the five rows of the "Species to Watch For" card from the observation data.
    //Runs when the dashboard loads, and again after anything is added or deleted.
    private void fillSpeciesCard(ObservationManager manager) {
        if (speciesRow1 == null) {
            return; // Shows nothing if the first piece of the UI is missing.
        }

        // The controller holds the row's pieces as 20 separate @FXML fields. Bundling each
        // row's four nodes into one SpeciesRow lets the loop below work with one array
        SpeciesRow[] rows = {
                new SpeciesRow(speciesRow1, speciesName1, speciesStat1, speciesBar1),
                new SpeciesRow(speciesRow2, speciesName2, speciesStat2, speciesBar2),
                new SpeciesRow(speciesRow3, speciesName3, speciesStat3, speciesBar3),
                new SpeciesRow(speciesRow4, speciesName4, speciesStat4, speciesBar4),
                new SpeciesRow(speciesRow5, speciesName5, speciesStat5, speciesBar5)
        };

        // Species totals, already sorted biggest first by the manager
        Map<String, Integer> birdsBySpecies = manager.getBirdsBySpecies();

        // Copies the names into a list we can index
        List<String> species = List.copyOf(birdsBySpecies.keySet());

        // Every bird the community counted; each bar is measured against this
        int totalBirds = manager.getTotalBirdsCounted();

        for (int i = 0; i < rows.length; i++) {
            if (i >= species.size()) {
                rows[i].hide(); // fewer species than rows, so hide the leftovers
                continue;
            }
            String name = species.get(i);
            int birds = birdsBySpecies.get(name);
            // The last argument is this species' share of every bird counted, which sets
            // how far the bar fills. The check avoids dividing by zero on an empty file.
            rows[i].show(name, birds, manager.getSightingCountFor(name),
                    totalBirds == 0 ? 0 : (double) birds / totalBirds);
        }
    }


     // One row of the species card
     // A record is a short way to write a class that only holds values, so these four
     // nodes can travel together instead of being passed around separately.
    private record SpeciesRow(VBox box, Label name, Label stat, ProgressBar bar) {

        // Takes the row off the screen and gives its space back to the rows above
        void hide() {
            box.setVisible(false);  // stop drawing it
            box.setManaged(false);  // and stop the layout saving room for it, or a gap is left
        }

        // Puts one species' numbers into this row
        void show(String species, int birds, int sightings, double share) {
            box.setVisible(true);
            box.setManaged(true);

            name.setText(species);
            // Reads like "37 birds across 19 sightings"
            stat.setText(plural(birds, "bird") + " across " + plural(sightings, "sighting"));
            stat.setTextFill(Color.web(ACCENT));

            bar.setProgress(share); // 0.0 is empty, 1.0 is full
            bar.setStyle("-fx-accent: " + BAR_COLOR + "; -fx-control-inner-background: #FFFFFF;");
        }

        // Adds the "s" only when it's needed: 1 bird / 2 birds
        private static String plural(int count, String word) {
            return count + " " + word + (count == 1 ? "" : "s");
        }
    }

    // Returns the user's first name if we have it
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

    @FXML
    private void handleLogout(ActionEvent event) {
        UserManager.shared().logout();
        NavigationUtil.switchScreen(mainStage, "login-screen.fxml");
    }

    //Switch to my-observations screen
    @FXML
    private void handleObservations(MouseEvent event) {
        NavigationUtil.switchScreen(mainStage, "my-observations.fxml");
    }

    //Open the add-observation popup, then update the numbers it may have changed
    @FXML
    private void handleAdd() {
        NavigationUtil.showPopup(mainStage, "add-observation.fxml");
        refresh();
    }

    @FXML
    private void handleUser(ActionEvent event) {
        NavigationUtil.showPopup(mainStage, "profile-screen.fxml");
    }

}
