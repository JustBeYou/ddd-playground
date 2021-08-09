package ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Application {
  private final Executor executor;

  public Application() {
    executor = new Executor(new Command[]{
      new Command(
        "/login", "<user> <password>",
        "Creates a new session.",
        (String[] args) -> {
        return CommandStatus.SUCCESS;
      })
    });
  }

  public void run() {
    System.out.println("--- Library management ---");

    String input;
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    while (true) {
      try {
        System.out.print("> ");
        input = reader.readLine();

        if (input.startsWith("help")) {
          var cmd = input.substring(5);
          executor.help(cmd);
        } else if (input.startsWith("describe")) {
          var cmd = input.substring(9);
          executor.describeModule(cmd);
        } else if (input.startsWith("exit")) {
          break;
        } else {
          executor.run(input);
        }
      } catch (CommandNotFoundException exception) {
        System.out.println("Command not found. Use: ");
        System.out.println("- help /some/command/here");
        System.out.println("- describe /some/module/here");
        System.out.println("- /some/command/here arg1 arg2 arg3");
      } catch (Exception exception) {
        System.out.println("Something went wrong: " + exception.getMessage());
      }
    }
  }
}
