package services.csv;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class CsvWriter {
    private BufferedWriter writer;

    public CsvWriter(BufferedWriter writer) {
        this.writer = writer;
    }

    public void writeRow(Collection<String> row) throws IOException {
        this.writer.write(String.join(",", row) + "\n");
        this.writer.flush();
    }
}
