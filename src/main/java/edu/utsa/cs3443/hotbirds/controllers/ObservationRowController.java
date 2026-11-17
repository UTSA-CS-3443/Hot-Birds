package edu.utsa.cs3443.hotbirds.controllers;

import edu.utsa.cs3443.hotbirds.model.Observation;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.Map;
import java.util.function.Consumer;

public class ObservationRowController {
    @FXML private Label nameLabel, countLabel, locationLabel, dateLabel;

    @FXML private Circle background;
    @FXML private ImageView birdIcon;

    // Different color values for each bird in observation list.
    private static final Map<String, String> SPECIES_COLORS = Map.of(
            "American Goldfinch",     "#F3E3A0",
            "Eastern Bluebird",       "#B8D4F0",
            "Painted Bunting",        "#F2B8C0",
            "Spotted Towhee",         "#DCD2CB",
            "Red-breasted Nuthatch",  "#F0C9A8"
    );
    private static final Map<String, String> ICON_COLORS = Map.of(
            "American Goldfinch",     "#8A6D00",
            "Eastern Bluebird",       "#1F4E8C",
            "Painted Bunting",        "#9E1B30",
            "Spotted Towhee",         "#3B2F2A",
            "Red-breasted Nuthatch",  "#A1521F"
    );

    // Used when the species has no colour of its own yet
    private static final String DEFAULT_CIRCLE = "#DDE3E0";
    private static final String DEFAULT_ICON = "#4A5A54";

    private Observation item;
    private Consumer<Observation> onEdit, onDelete;

    // Builds row using data given by an Observation.
    public void setData(Observation item, Consumer<Observation> onEdit, Consumer<Observation> onDelete) {
        this.item = item;
        this.onEdit = onEdit;
        this.onDelete = onDelete;

        nameLabel.setText(item.getSpecies());
        countLabel.setText(Integer.toString(item.getCount()));
        locationLabel.setText(item.getLocation());
        dateLabel.setText(item.getDisplayDate());

        String species = item.getSpecies();
        background.setFill(Color.web(SPECIES_COLORS.getOrDefault(species, DEFAULT_CIRCLE)));
        tintIcon(species);
    }

    // Recolours the bird picture to match the species.
    private void tintIcon(String species) {
        Color tint = Color.web(ICON_COLORS.getOrDefault(species, DEFAULT_ICON));
        ColorInput block = new ColorInput(0, 0, birdIcon.getFitWidth(), birdIcon.getFitHeight(), tint);
        birdIcon.setEffect(new Blend(BlendMode.SRC_ATOP, null, block));
    }

    @FXML private void handleEdit() { onEdit.accept(item); }
    @FXML private void handleDelete() { onDelete.accept(item); }
}
