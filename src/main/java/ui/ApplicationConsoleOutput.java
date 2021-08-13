package ui;

public class ApplicationConsoleOutput implements ApplicationOutput {

    @Override
    public void writeLine(String line) {
        System.out.println(line);
    }

    @Override
    public void write(String line) {
        System.out.print(line);
    }
}
