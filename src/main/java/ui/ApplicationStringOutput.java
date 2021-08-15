package ui;

public class ApplicationStringOutput implements ApplicationOutput {
    private final StringBuilder output;

    public ApplicationStringOutput() {
        output = new StringBuilder();
    }

    public String dump() {
        return output.toString();
    }

    @Override
    public void writeLine(String line) {
        output.append(line).append("\n");
    }

    @Override
    public void write(String line) {
        output.append(line);
    }
}
