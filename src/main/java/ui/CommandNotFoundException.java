package ui;

public class CommandNotFoundException extends Exception {
    public CommandNotFoundException(String path) {
        super("Command under path: " + path + " not found.");
    }
}
