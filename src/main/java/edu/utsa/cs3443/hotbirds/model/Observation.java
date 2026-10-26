package edu.utsa.cs3443.hotbirds.model;

public class Observation {
    private String species;
    private String observer;
    private int count;
    private String location;
    private String date;

    public Observation(String species, String observer, int count, String location, String date) {
        this.species = species;
        this.observer = observer;
        this.count = count;
        this.location = location;
        this.date = date;
    }

    public String getSpecies() {
        return species;
    }
    public String getObserver() {
        return observer;
    }
    public int getCount() {
        return count;
    }
    public String getLocation() {
        return location;
    }
    public String getDate() {
        return date;
    }

    public void setSpecies(String species) {
        this.species = species;
    }
    public void setObserver(String observer) {
        this.observer = observer;
    }
    public void setCount(int count) {
        this.count = count;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public void setDate(String date) {
        this.date = date;
    }
}
