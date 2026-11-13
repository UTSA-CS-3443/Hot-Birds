package edu.utsa.cs3443.hotbirds.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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

    public Observation convertLineToObservation(String line) {
        List<String> f = splitCsv(line);
        if (f.size() < 8) {
            return null; // malformed row
        }
        try {
            String observer = f.get(0);
            String species = f.get(1);
            int count = Integer.parseInt(f.get(2));
            String time = f.get(3);
            String amPm = f.get(4);
            String date = f.get(5);
            String nickname = f.get(f.size() - 1);
            // an unquoted address with commas gets split, so rejoin the middle columns
            String address = String.join(", ", f.subList(6, f.size() - 1));
            return new Observation(observer, species, count, time, amPm, date, address, nickname);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public String convertObservationToLine(Observation o) {
        return String.join(",",
                csv(o.getObserver()), csv(o.getSpecies()), String.valueOf(o.getCount()),
                csv(o.getTime()), csv(o.getAmPm()), csv(o.getDate()),
                csv(o.getAddress()), csv(o.getLocation()));
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

     //Adds up how many birds of each species everyone has logged, biggest total first.
     //The dashboard's "Species to Watch For" card reads the first five.
    public java.util.LinkedHashMap<String, Integer> getBirdsBySpecies() {

        // Step 1: add up the quantities, one running total per species.
        // The key is the species name, the value is the birds counted so far.
        java.util.HashMap<String, Integer> totals = new java.util.HashMap<>();
        for (Observation o : observationArrayList) {

            // merge() means: if this species isn't in the map yet, put its count in;
            // if it is, combine the old value and the new one with Integer::sum (old + new).
            totals.merge(o.getSpecies(), o.getCount(), Integer::sum);
        }

        // Step 2: put the species names in order, biggest total first.
        java.util.List<String> names = new ArrayList<>(totals.keySet());

        // The comparator returns a negative number when x should come before y.
        // Subtracting y's total from x's puts the BIGGER total first (descending).
        names.sort((x, y) -> totals.get(y) - totals.get(x));

        // Step 3: copy into a LinkedHashMap, which keeps the order things were added.
        java.util.LinkedHashMap<String, Integer> sorted = new java.util.LinkedHashMap<>();
        for (String name : names) {
            sorted.put(name, totals.get(name));
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

    private static String csv(String value) {
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private static List<String> splitCsv(String line) {
        List<String> out = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    sb.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                out.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        out.add(sb.toString().trim());
        return out;
    }
}