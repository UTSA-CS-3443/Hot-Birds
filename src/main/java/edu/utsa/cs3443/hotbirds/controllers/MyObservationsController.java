package edu.utsa.cs3443.hotbirds.controllers;

import edu.utsa.cs3443.hotbirds.HotBirdsApplication;
import edu.utsa.cs3443.hotbirds.model.Observation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import java.io.IOException;

public class MyObservationsController {
    @FXML private ListView<Observation> listView;

    private final ObservableList<Observation> observations = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        listView.setItems(observations);
        listView.setCellFactory(lv -> new ListCell<>() {
            private Node root;
            private ObservationRowController rowController;
            {
                try {
                    FXMLLoader loader = new FXMLLoader(HotBirdsApplication.class.getResource("layouts/observation-row.fxml"));
                    root = loader.load();
                    rowController = loader.getController();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }



            @Override
            public void updateItem(Observation item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    rowController.setData(item, MyObservationsController.this::edit, observations::remove);
                    setGraphic(root);
                }
            }
        });

        //test observations -----
        observations.add(new Observation("American Goldfinch", "redsparrow", 2, "Walker Ranch Park", "Sep 2 · 12:00 PM"));
        observations.add(new Observation("Eastern Bluebird", "redsparrow", 1, "Leon Creek Greenway", "Sep 1 · 9:00 AM"));
        observations.add(new Observation("Painted Bunting", "redsparrow", 1, "Crescent Bend Nature Park", "Aug 23 · 5:30 PM"));
        observations.add(new Observation("Spotted Towhee", "greyduckling", 1, "Landa Park", "Oct 2 · 7:00 AM"));
        observations.add(new Observation("Red-breasted Nuthatch", "greyduckling", 1, "Crescent Bend Nature Park", "Sep 25 · 5:00 PM"));
        observations.add(new Observation("Great Blue Heron", "Maya", 1, "Lower Creek Bend", "Sep 27 · 5:38 PM"));
        observations.add(new Observation("Red-bellied Woodpecker", "Maya", 2, "Oak Ridge Trail", "Sep 27 · 4:12 PM"));
        observations.add(new Observation("American Goldfinch", "redsparrow", 2, "Walker Ranch Park", "Sep 2 · 12:00 PM"));
        observations.add(new Observation("Eastern Bluebird", "redsparrow", 1, "Leon Creek Greenway", "Sep 1 · 9:00 AM"));
        observations.add(new Observation("Painted Bunting", "redsparrow", 1, "Crescent Bend Nature Park", "Aug 23 · 5:30 PM"));
        observations.add(new Observation("Spotted Towhee", "greyduckling", 1, "Landa Park", "Oct 2 · 7:00 AM"));
        observations.add(new Observation("Red-breasted Nuthatch", "greyduckling", 1, "Crescent Bend Nature Park", "Sep 25 · 5:00 PM"));
        observations.add(new Observation("Great Blue Heron", "Maya", 1, "Lower Creek Bend", "Sep 27 · 5:38 PM"));
        observations.add(new Observation("Red-bellied Woodpecker", "Maya", 2, "Oak Ridge Trail", "Sep 27 · 4:12 PM"));
        observations.add(new Observation("American Goldfinch", "redsparrow", 2, "Walker Ranch Park", "Sep 2 · 12:00 PM"));
        observations.add(new Observation("Eastern Bluebird", "redsparrow", 1, "Leon Creek Greenway", "Sep 1 · 9:00 AM"));
        observations.add(new Observation("Painted Bunting", "redsparrow", 1, "Crescent Bend Nature Park", "Aug 23 · 5:30 PM"));
        observations.add(new Observation("Spotted Towhee", "greyduckling", 1, "Landa Park", "Oct 2 · 7:00 AM"));
        observations.add(new Observation("Red-breasted Nuthatch", "greyduckling", 1, "Crescent Bend Nature Park", "Sep 25 · 5:00 PM"));
        observations.add(new Observation("Great Blue Heron", "Maya", 1, "Lower Creek Bend", "Sep 27 · 5:38 PM"));
        observations.add(new Observation("Red-bellied Woodpecker", "Maya", 2, "Oak Ridge Trail", "Sep 27 · 4:12 PM"));
        observations.add(new Observation("American Goldfinch", "redsparrow", 2, "Walker Ranch Park", "Sep 2 · 12:00 PM"));
        observations.add(new Observation("Eastern Bluebird", "redsparrow", 1, "Leon Creek Greenway", "Sep 1 · 9:00 AM"));
        observations.add(new Observation("Painted Bunting", "redsparrow", 1, "Crescent Bend Nature Park", "Aug 23 · 5:30 PM"));
        observations.add(new Observation("Spotted Towhee", "greyduckling", 1, "Landa Park", "Oct 2 · 7:00 AM"));
        observations.add(new Observation("Red-breasted Nuthatch", "greyduckling", 1, "Crescent Bend Nature Park", "Sep 25 · 5:00 PM"));
        observations.add(new Observation("Great Blue Heron", "Maya", 1, "Lower Creek Bend", "Sep 27 · 5:38 PM"));
        observations.add(new Observation("Red-bellied Woodpecker", "Maya", 2, "Oak Ridge Trail", "Sep 27 · 4:12 PM"));
        // ----------------------
    }
    public void add(Observation observation) {observations.add(observation);}

    public void edit(Observation observation) {
        //open edit ui
        listView.refresh();
    }
}
