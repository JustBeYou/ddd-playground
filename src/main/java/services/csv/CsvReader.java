package services.csv;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class CsvReader {
    private final String filename;

    public CsvReader(String filename) {
        this.filename = filename;
    }

    public CsvData read() throws FileNotFoundException, InvalidRowLength {
        var reader = new BufferedReader(new FileReader(filename));
        int i = 0;
        CsvData data = null;
        while (true) {
            try {
                var line = reader.readLine();
                var parts = line.split(",");
                var row = new ArrayList<String>(Arrays.asList(parts));

                if (i == 0) {
                    data = new CsvData(row);
                } else {
                    data.addRow(row);
                }
            } catch (IOException ignored) {
                break;
            }
            i++;
        }

        return data;
    }
}
