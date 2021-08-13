package ui;

import lombok.Data;
import lombok.NonNull;

import java.util.Arrays;

@Data
public class Executor {
    @NonNull Command[] commands;
    @NonNull ApplicationOutput appOutput;

    public void help(String path) throws CommandNotFoundException {
        var command = findCommand(path);
        appOutput.writeLine("Command usage: " + path + " " + String.join(" ", command.args) +
            "\nDescription: " + command.getDescription() + "\n");
    }

    public void describeModule(String path) {
        var parsedPath = parseCommand(path);
        appOutput.writeLine("Commands under: " + path);
        for (var command : commands) {
            if (command.isInPath(parsedPath)) {
                appOutput.writeLine("- " + String.join("/", command.getPath()) + ": " + command.getDescription());
            }
        }
    }

    public void run(String path) throws Exception {
        var pathAndArgs = parseCommandArgs(path);
        var command = findCommand(pathAndArgs[0]);

        if (command.getArgs().length != pathAndArgs.length - 1) {
            appOutput.writeLine("Invalid arguments.");
            this.help(pathAndArgs[0]);
            return;
        }

        var result = command.getAction().apply(Arrays.copyOfRange(pathAndArgs, 1, pathAndArgs.length));
        if (result == CommandStatus.FAIL) {
            appOutput.writeLine("Command failed.");
        } else if (result == CommandStatus.SUCCESS) {
            appOutput.writeLine("Command successfully executed.");
        }
    }

    private Command findCommand(String path) throws CommandNotFoundException {
        var parsedPath = parseCommand(path);
        for (var command : commands) {
            if (command.isSamePath(parsedPath)) {
                return command;
            }
        }
        throw new CommandNotFoundException(path);
    }

    private String[] parseCommand(String path) {
        return path.split("/");
    }

    private String[] parseCommandArgs(String path) {
        return path.split(" ");
    }
}