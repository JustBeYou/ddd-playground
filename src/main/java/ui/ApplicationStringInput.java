package ui;

import java.io.IOException;
import java.util.ArrayList;

public class ApplicationStringInput implements ApplicationInput {
    private final ArrayList<String> lines;
    private int index;

    public ApplicationStringInput() {
        lines = new ArrayList<>();
        index = 0;
    }

    public void feed(String line) {
        lines.add(line);
    }

    @Override
    public String readLine() throws IOException {
        if (index >= lines.size()) {
            throw new IOException("EOF");
        }

        return lines.get(index++);
    }
}
