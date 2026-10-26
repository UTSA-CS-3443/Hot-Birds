package edu.utsa.cs3443.hotbirds.controllers;

import edu.utsa.cs3443.hotbirds.model.Observation;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.util.function.Consumer;

public class ObservationRowController {
    @FXML private Label nameLabel, observerLabel, countLabel, locationLabel, dateLabel;

    @FXML private javafx.scene.shape.Circle avatar;
    @FXML private javafx.scene.image.ImageView birdIcon;

    private static final java.util.Map<String, String> SPECIES_COLORS = java.util.Map.of(
            "American Goldfinch",     "#F3E3A0",
            "Eastern Bluebird",       "#B8D4F0",
            "Painted Bunting",        "#F2B8C0",
            "Spotted Towhee",         "#E8C3A0",
            "Red-breasted Nuthatch",  "#F0C9A8",
            "Great Blue Heron",       "#C5D3DE",
            "Red-bellied Woodpecker", "#EBC9C5"
    );

    private static final java.util.Map<String, String> ICON_COLORS = java.util.Map.of(
            "American Goldfinch",     "#8A6D00",
            "Eastern Bluebird",       "#1F4E8C",
            "Painted Bunting",        "#9E1B30",
            "Spotted Towhee",         "#8A4310",
            "Red-breasted Nuthatch",  "#9A5420",
            "Great Blue Heron",       "#3A5670",
            "Red-bellied Woodpecker", "#8C2B20"
    );

    private Observation item;
    private Consumer<Observation> onEdit, onDelete;

    public void setData(Observation item, Consumer<Observation> onEdit, Consumer<Observation> onDelete){
        this.item = item;
        this.onEdit = onEdit;
        this.onDelete = onDelete;
        nameLabel.setText(item.getSpecies());
        observerLabel.setText(item.getObserver());
        countLabel.setText(Integer.toString(item.getCount()));
        locationLabel.setText(item.getLocation());
        dateLabel.setText(item.getDate());

        String species = item.getSpecies();
        avatar.setFill(javafx.scene.paint.Color.web(SPECIES_COLORS.getOrDefault(species, "#DDE3E0")));
        birdIcon.setEffect(new javafx.scene.effect.Blend(javafx.scene.effect.BlendMode.SRC_ATOP, null, new javafx.scene.effect.ColorInput(0, 0, birdIcon.getFitWidth(), birdIcon.getFitHeight(), javafx.scene.paint.Color.web(ICON_COLORS.getOrDefault(species, "#4A5A54")))));
    }

    @FXML private void handleEdit() { onEdit.accept(item); }
    @FXML private void handleDelete() { onDelete.accept(item); }
}
