package services;

import services.csv.CsvWriter;

import java.io.*;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;

public class CsvLoggerService implements LoggerService {
    private final CsvWriter writer;
    private static final String[] header = new String[]{
        "timestamp",
        "message"
    };

    public CsvLoggerService(String filename) throws IOException {
        this.writer = new CsvWriter(new BufferedWriter(new FileWriter(filename, true)));

        try {
            var reader = new BufferedReader(new FileReader(filename));
            var firstLine = reader.readLine();
            if (!firstLine.equals(String.join(",", header))) {
                throw new Exception("Should add header");
            }
        } catch (Exception ignored) {
            this.writer.writeRow(Arrays.asList(header));
        }
    }

    public void log(String message) {
        try {
            var timestamp = new Timestamp(new Date().getTime());
            this.writer.writeRow(Arrays.asList(timestamp.toString(),
                message));
        } catch (Exception ex) {
            System.out.println("Couldn't log: " + ex.getMessage());
        }
    }
}
