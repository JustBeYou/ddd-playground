package services;

import services.csv.CsvData;
import services.csv.CsvWriter;
import services.csv.InvalidRowLength;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;

public class CsvLoggerService implements LoggerService {
    private final String filename;
    private final CsvData data;

    public CsvLoggerService(String filename) {
        this.filename = filename;
        this.data = new CsvData(Arrays.asList("timestamp", "message"));
    }

    public void log(String message) {
        try {
            data.addRow(Arrays.asList(
                new Timestamp(System.currentTimeMillis()).toString(),
                message
            ));
        } catch (InvalidRowLength ignored) {

        }

        try {
            new CsvWriter(filename).write(data);
        } catch (IOException ex) {
            System.out.println("Could not log: " + ex.getMessage());
        }
    }
}
