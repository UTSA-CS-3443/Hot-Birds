package edu.utsa.cs3443.hotbirds.util;

import java.util.ArrayList;
import java.util.List;


//The CSV rules both managers share: how a value is written out, and how a saved line is read back.

public final class CsvUtil {

    private CsvUtil() { }

    // Wraps a value in quotes only if it contains a comma or a quote of its own
    public static String quote(String value) {
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    // Splits one CSV line, keeping commas that sit inside quotes
    public static List<String> split(String line) {
        List<String> out = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    sb.append('"'); // two quotes in a row means one real quote
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                out.add(sb.toString().trim()); // end of a value
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        out.add(sb.toString().trim()); // the last value has no comma after it
        return out;
    }
}
