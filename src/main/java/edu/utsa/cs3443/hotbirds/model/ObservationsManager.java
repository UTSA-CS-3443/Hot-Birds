package edu.utsa.cs3443.hotbirds.model;

import java.io.*;
import java.util.ArrayList;

public class ObservationsManager {
    ArrayList<Observation> observationArrayList;
    String filePath = "data/observations.csv";

    public ObservationsManager() {
        observationArrayList = new ArrayList<Observation>();
    }

    public void addObservation(Observation observation) {
        observationArrayList.add(observation);
    }

    public void loadObservations() throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;

        String header = br.readLine();

        while ((line = br.readLine()) != null) {
            Observation o = convertLineToObservation(line);

            addObservation(o);
        }
    }

    public Observation findObservation(String species) {
        for(Observation o: observationArrayList) {
            if(o.getSpecies().equals(species)) {
                return o;
            }
        }
        return null;
    }

    public boolean doesObservationExist(String species) {
        if (findObservation(species) != null) {
            return true;
        }

        return false;
    }

    public boolean updateObservation(Observation observation) throws FileNotFoundException, IOException {
        if (doesObservationExist(observation.getSpecies())) {
            for(Observation o: observationArrayList) {
                if(o.getSpecies().equals(observation.getSpecies())) {
                    observationArrayList.remove(o);
                }
            }

            observationArrayList.add(observation);

            saveDataToFile();

            return true;
        }

        return false;
    }

    public boolean deleteObservation(Observation observation) throws FileNotFoundException, IOException {
        if (doesObservationExist(observation.getSpecies())) {
            for(Observation o: observationArrayList) {
                if(o.getSpecies().equals(observation.getSpecies())) {
                    observationArrayList.remove(o);
                }
            }

            saveDataToFile();

            return true;
        }

        return false;
    }

    public void saveDataToFile() throws FileNotFoundException, IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));

        bw.write("name,registration number,tonnage,crew size,port,aid type,aid capacity,has helipad");
        bw.newLine();

        for(Observation o: observationArrayList) {
            String line = convertObservationToLine(o);

            bw.write(line);
            bw.newLine();
        }

        bw.newLine();
    }

    public Observation convertLineToObservation(String line) {
        String[] values = line.split(",");

        String species = values[0].trim();
        String observer = values[1].trim();
        int count = Integer.parseInt(values[2].trim());
        String location = values[3].trim();
        String date = values[4].trim();

        Observation result = new Observation(species, observer, count, location, date);
        return result;
    }

    public String convertObservationToLine(Observation observation) {
        String line = String.format("%s,%s,%s,%s,%s",
                observation.getSpecies(),
                observation.getObserver(),
                observation.getCount(),
                observation.getLocation(),
                observation.getDate()
        );

        return line;
    }

    public ArrayList getObservationArrayList() {
        return observationArrayList;
    }
}
