package ui;

import lombok.Data;
import lombok.NonNull;
import services.CsvLoggerService;
import services.LoggerService;
import services.SessionStatefulService;

import java.util.ArrayList;
import java.util.Arrays;

@Data
public class Executor {
    @NonNull private ArrayList<Command> commands;
    @NonNull private ApplicationOutput appOutput;
    @NonNull private SessionStatefulService sessionService;
    @NonNull private LoggerService loggerService;

    public Executor(
        ApplicationOutput appOutput,
        SessionStatefulService sessionService,
        LoggerService loggerService
    ) {
        this.appOutput = appOutput;
        this.sessionService = sessionService;
        this.commands = new ArrayList<>();
        this.loggerService = loggerService;
    }

    public void registerModule(Module module) {
        for (var command: module.getCommands()) {
            commands.add(command);
        }
    }

    public void help(String path) throws CommandNotFoundException {
        var command = findCommand(path);
        appOutput.writeLine("Command usage: " + path + " " + String.join(" ", command.args) +
            "\nDescription: " + command.getDescription() + "\n");
    }

    public void describeModule(String path) {
        var parsedPath = parseCommand(path);
        appOutput.writeLine("Commands under: " + path);
        for (var command : commands) {
            if (command.isInPath(parsedPath) && isAvailable(command)) {
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

        var args = Arrays.copyOfRange(pathAndArgs, 1, pathAndArgs.length);

        var result = command.getAction().apply(args);
        if (result == CommandStatus.FAIL) {
            appOutput.writeLine("Command failed.");
        } else if (result == CommandStatus.SUCCESS) {
            appOutput.writeLine("Command successfully executed.");
        }

        loggerService.log("User called action " + String.join("/", command.getPath()) +
            " with arguments (" + String.join(" ", args) +
            ") and returned status " + result.toString() + ".");
    }

    private Command findCommand(String path) throws CommandNotFoundException {
        var parsedPath = parseCommand(path);
        for (var command : commands) {
            if (command.isSamePath(parsedPath) && isAvailable(command)) {
                return command;
            }
        }
        throw new CommandNotFoundException(path);
    }

    private boolean isAvailable(Command command) {
        var isLoggedIn = sessionService.isLoggedIn();

        if (!isLoggedIn && command.necessaryRights != null) {
            return false;
        }

        if (isLoggedIn && command.necessaryRights == null) {
            return false;
        }

        if (command.necessaryRights != null) {
            var user = sessionService.getCurrentUser();
            for (var right : command.necessaryRights) {
                if (!user.hasRight(right)) {
                    return false;
                }
            }
        }

        return true;
    }

    private String[] parseCommand(String path) {
        return path.split("/");
    }

    private String[] parseCommandArgs(String path) {
        return path.split(" ");
    }
}
