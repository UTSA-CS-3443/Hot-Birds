package edu.utsa.cs3443.hotbirds.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class Observation {
    private String observer;
    private String species;
    private int count;
    private String time;
    private String amPm;
    private String date;
    private String address;
    private String location;

    public Observation(String observer, String species, int count, String time, String amPm,
                       String date, String address, String location) {
        this.observer = observer;
        this.species = species;
        this.count = count;
        this.time = time;
        this.amPm = amPm;
        this.date = date;
        this.address = address;
        this.location = location;
    }

    public String getObserver() { return observer; }
    public String getSpecies() { return species; }
    public int getCount() { return count; }
    public String getTime() { return time; }
    public String getAmPm() { return amPm; }
    public String getDate() { return date; }
    public String getAddress() { return address; }
    public String getLocation() { return location; }

    // What the list shows: "Sep 2 · 12:00 PM"
    public String getDisplayDate() {
        String day = date;
        try {
            day = LocalDate.parse(date, DateTimeFormatter.ofPattern("M/d/yyyy"))
                    .format(DateTimeFormatter.ofPattern("MMM d", Locale.US));
        } catch (DateTimeParseException ignored) { }
        return day + " · " + time + " " + amPm;
    }

    public void setObserver(String observer) { this.observer = observer; }
    public void setSpecies(String species) { this.species = species; }
    public void setCount(int count) { this.count = count; }
    public void setTime(String time) { this.time = time; }
    public void setAmPm(String amPm) { this.amPm = amPm; }
    public void setDate(String date) { this.date = date; }
    public void setAddress(String address) { this.address = address; }
    public void setLocation(String location) { this.location = location; }
}