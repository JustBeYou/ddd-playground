package services.csv;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CsvWriter {
    private final String filename;

    public CsvWriter(String filename) {
        this.filename = filename;
    }

    public void write(CsvData data) throws IOException {
        var writer = new BufferedWriter(new FileWriter(filename));
        writer.write(String.join(",", data.getHeader()) + "\n");
        for (var row: data.getRows()) {
            writer.write(String.join(",", row) + "\n");
        }
        writer.close();
    }
}
