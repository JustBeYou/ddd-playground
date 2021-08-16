package services.csv;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;

public class CsvData {
    @Getter private final ArrayList<String> header;
    @Getter private final ArrayList<ArrayList<String>> rows;

    public CsvData(Collection<String> header) {
        this.header = new ArrayList<>();
        this.header.addAll(header);
        this.rows = new ArrayList<>();
    }

    public void addRow(Collection<String> row) throws InvalidRowLength {
        if (row.size() != header.size()) {
            throw new InvalidRowLength();
        }
        rows.add(new ArrayList<>(row));
    }
}
