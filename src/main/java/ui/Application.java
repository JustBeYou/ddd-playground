package ui;

import auth.Identity;
import persistence.models.InMemoryRepositoryFactory;

public class Application {
    private final Executor executor;
    private final ApplicationInput appInput;
    private final ApplicationOutput appOutput;
    private Identity identity;

    public Application(ApplicationInput appInput, ApplicationOutput appOutput) {
        this.appInput = appInput;
        this.appOutput = appOutput;

        var repoFactory = new InMemoryRepositoryFactory();
        var userRepo = repoFactory.build("User");

        this.executor = new Executor(new Command[]{
            new Command(
                "/login", "<user> <password>",
                "Creates a new session.",
                (String[] args) -> {
                    var user = args[0];
                    var password = args[1];

                    return CommandStatus.SUCCESS;
                })
        }, this.appOutput);
    }

    public void run() {
        appOutput.writeLine("--- Library management ---");

        String inputLine;
        while (true) {
            try {
                appOutput.write("> ");
                inputLine = appInput.readLine();

                if (inputLine.startsWith("help ")) {
                    var cmd = inputLine.substring(5);
                    executor.help(cmd);
                } else if (inputLine.startsWith("describe ")) {
                    var cmd = inputLine.substring(9);
                    executor.describeModule(cmd);
                } else if (inputLine.startsWith("exit")) {
                    break;
                } else {
                    executor.run(inputLine);
                }
            } catch (CommandNotFoundException exception) {
                appOutput.writeLine("Command not found. Use: ");
                appOutput.writeLine("- help /some/command/here");
                appOutput.writeLine("- describe /some/module/here");
                appOutput.writeLine("- /some/command/here arg1 arg2 arg3");
            } catch (Exception exception) {
                appOutput.writeLine("Something went wrong: " + exception.getMessage());
            }
        }
    }
}
