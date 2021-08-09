package ui;

import lombok.Data;
import lombok.NonNull;

import java.util.Arrays;

@Data
public class Executor {
  @NonNull Command[] commands;

  public void help(String path) throws CommandNotFoundException {
    var command = findCommand(path);
    System.out.println("Command usage: " + path + " " + String.join(" ", command.args) +
      "\nDescription: " + command.getDescription() + "\n");
  }

  public void describeModule(String path) {
    var parsedPath = parseCommand(path);
    System.out.println("Commands under: " + path);
    for (var command: commands) {
      if (command.isInPath(parsedPath)) {
        System.out.println("- " + String.join("/",  command.getPath()) + ": " + command.getDescription());
      }
    }
  }

  public void run(String path) throws Exception {
    var pathAndArgs = parseCommandArgs(path);
    var command = findCommand(pathAndArgs[0]);

    if (command.getArgs().length != pathAndArgs.length - 1) {
      System.out.println("Invalid arguments.");
      this.help(pathAndArgs[0]);
      return;
    }

    var result = command.getAction().apply(Arrays.copyOfRange(pathAndArgs, 1, pathAndArgs.length));
    if (result == CommandStatus.FAIL) {
      System.out.println("Command failed.");
    } else if (result == CommandStatus.SUCCESS) {
      System.out.println("Command successfully executed.");
    }
  }

  private Command findCommand(String path) throws CommandNotFoundException {
    var parsedPath = parseCommand(path);
    for (var command: commands) {
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
