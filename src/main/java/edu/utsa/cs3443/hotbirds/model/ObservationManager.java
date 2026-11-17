package edu.utsa.cs3443.hotbirds.model;

import edu.utsa.cs3443.hotbirds.util.CsvUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class ObservationManager {
    private static final String HEADER = "User,Species,Quantity,Time,AM or PM,Date,Location,Location Nickname";

    // One shared manager for the whole app, so every screen sees the same data
    private static ObservationManager shared;

    private final ArrayList<Observation> observationArrayList = new ArrayList<>();
    private final Path filePath;

    public ObservationManager() {
        this("data/observations.csv");
    }

    public ObservationManager(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * The one manager the screens share. Loads the CSV the first time it is called,
     * so every screen reads the same list instead of its own copy.
     */
    public static ObservationManager shared() {
        if (shared == null) {
            shared = new ObservationManager();
            try {
                shared.loadObservations();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return shared;
    }

    public void addObservation(Observation observation) {
        observationArrayList.add(observation);
    }

    public void loadObservations() throws IOException {
        observationArrayList.clear();
        if (!Files.exists(filePath)) {
            return; // nothing saved yet
        }
        try (BufferedReader br = Files.newBufferedReader(filePath)) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                Observation o = convertLineToObservation(line);
                if (o != null) {
                    observationArrayList.add(o);
                }
            }
        }
    }

    public Observation findObservation(String species) {
        for (Observation o : observationArrayList) {
            if (o.getSpecies().equals(species)) {
                return o;
            }
        }
        return null;
    }

    public boolean doesObservationExist(String species) {
        return findObservation(species) != null;
    }

    // Edits change the Observation object itself, so updating just means saving.
    public boolean updateObservation(Observation observation) throws IOException {
        if (!observationArrayList.contains(observation)) {
            return false;
        }
        saveDataToFile();
        return true;
    }

    // Removes this exact observation (not every one with the same species).
    public boolean deleteObservation(Observation observation) throws IOException {
        int index = observationArrayList.indexOf(observation);
        if (index < 0) {
            return false;
        }
        observationArrayList.remove(index);
        try {
            saveDataToFile();
            return true;
        } catch (IOException e) {
            observationArrayList.add(index, observation); // undo if the file write failed
            throw e;
        }
    }

    public void saveDataToFile() throws IOException {
        Path parent = filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (BufferedWriter bw = Files.newBufferedWriter(filePath)) {
            bw.write(HEADER);
            bw.newLine();
            for (Observation o : observationArrayList) {
                bw.write(convertObservationToLine(o));
                bw.newLine();
            }
        }
    }

    // Turns one saved line back into an Observation, in header order:
    // User, Species, Quantity, Time, AM or PM, Date, Location, Location Nickname
    public Observation convertLineToObservation(String line) {
        List<String> fields = CsvUtil.split(line);
        if (fields.size() != 8) {
            return null; // malformed row
        }
        try {
            String observer = fields.get(0);
            String species = fields.get(1);
            int count = Integer.parseInt(fields.get(2));
            String time = fields.get(3);
            String amPm = fields.get(4);
            String date = fields.get(5);
            String address = fields.get(6);
            String nickname = fields.get(7);
            return new Observation(observer, species, count, time, amPm, date, address, nickname);
        } catch (NumberFormatException e) {
            return null; // the quantity column wasn't a number
        }
    }

    public String convertObservationToLine(Observation o) {
        return String.join(",",
                CsvUtil.quote(o.getObserver()), CsvUtil.quote(o.getSpecies()), String.valueOf(o.getCount()),
                CsvUtil.quote(o.getTime()), CsvUtil.quote(o.getAmPm()), CsvUtil.quote(o.getDate()),
                CsvUtil.quote(o.getAddress()), CsvUtil.quote(o.getLocation()));
    }

    // Adds the observation and writes the whole list back to the CSV
    public boolean saveNewObservation(Observation observation) throws IOException {
        observationArrayList.add(observation);
        try {
            saveDataToFile();
            return true;
        } catch (IOException e) {
            observationArrayList.remove(observation); // undo if the file write failed
            throw e;
        }
    }

    // Only the observations logged by this user (what My Observations shows)
    public ArrayList<Observation> getObservationsFor(String username) {
        ArrayList<Observation> mine = new ArrayList<>();
        if (username == null) {
            return mine;
        }
        for (Observation o : observationArrayList) {
            if (o.getObserver().equalsIgnoreCase(username.trim())) {
                mine.add(o);
            }
        }
        return mine;
    }

    // How many records this user logged
    public int getRecordCountFor(String username) {
        return getObservationsFor(username).size();
    }

    // How many records are in the whole file, from every user
    public int getTotalRecordCount() {
        return observationArrayList.size();
    }

    // Every bird counted by everyone (quantities added up, not rows)
    public int getTotalBirdsCounted() {
        int total = 0;
        for (Observation o : observationArrayList) {
            total += o.getCount();
        }
        return total;
    }

    // Adds up how many birds of each species everyone has logged, biggest total first.
    // The dashboard's "Species to Watch For" card reads the first five.
    public LinkedHashMap<String, Integer> getBirdsBySpecies() {

        // Step 1: add up the quantities, one running total per species.
        // The key is the species name, the value is the birds counted so far.
        HashMap<String, Integer> totals = new HashMap<>();
        for (Observation o : observationArrayList) {
            String species = o.getSpecies();
            int soFar = totals.getOrDefault(species, 0); // 0 the first time we see it
            totals.put(species, soFar + o.getCount());
        }

        // Step 2: pull the names out one at a time, biggest total first,
        // and add each to the result. A LinkedHashMap keeps that order.
        LinkedHashMap<String, Integer> sorted = new LinkedHashMap<>();
        List<String> remaining = new ArrayList<>(totals.keySet());
        while (!remaining.isEmpty()) {
            String biggest = remaining.get(0);
            for (String name : remaining) {
                if (totals.get(name) > totals.get(biggest)) {
                    biggest = name;
                }
            }
            sorted.put(biggest, totals.get(biggest));
            remaining.remove(biggest);
        }
        return sorted;
    }

    // How many separate sightings were logged for one species
    public int getSightingCountFor(String species) {
        int count = 0;
        for (Observation o : observationArrayList) {
            if (o.getSpecies().equals(species)) {
                count++;
            }
        }
        return count;
    }

    public ArrayList<Observation> getObservationArrayList() {
        return observationArrayList;
    }


}