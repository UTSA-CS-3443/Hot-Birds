package edu.utsa.cs3443.hotbirds.util;

import edu.utsa.cs3443.hotbirds.model.Observation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Shared form helpers for the Add and Edit screens, so both read and write the
 * observation fields the same way. It turns what a user types into a model
 * Observation, so it is a helper rather than a screen or stored data.
 */
public final class ObservationForm {
    public static final DateTimeFormatter CSV_DATE = DateTimeFormatter.ofPattern("M/d/yyyy");

    private ObservationForm() { }

    /**
     * Builds an Observation from what's typed on a form.
     * Returns null if a field is empty, the quantity isn't a number, or the time is unreadable.
     */
    public static Observation build(String observer, String species, String quantityText,
                             LocalDate date, String timeText, String location) {
        if (species == null || date == null || quantityText == null || timeText == null
                || location == null || quantityText.isBlank() || timeText.isBlank()
                || location.isBlank()) {
            return null;
        }

        int count;
        try {
            count = Integer.parseInt(quantityText.trim());
        } catch (NumberFormatException e) {
            return null;
        }
        if (count < 1) {
            return null;
        }

        // "6:40 AM" splits into the time and the AM/PM halves the CSV keeps separately
        String[] timeParts = timeText.trim().split("\\s+");
        if (timeParts.length != 2) {
            return null;
        }
        String time = timeParts[0];
        String amPm = timeParts[1].toUpperCase();
        if (!amPm.equals("AM") && !amPm.equals("PM")) {
            return null;
        }

        String user = (observer == null || observer.isBlank()) ? "guest" : observer.trim();
        String place = location.trim();
        // The form has one location box, so the address and the short name start out the same
        return new Observation(user, species, count, time, amPm, date.format(CSV_DATE), place, place);
    }

    // Reads a CSV date ("9/2/2026") back into something the DatePicker can show
    public static LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date, CSV_DATE);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
