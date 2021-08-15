package ui;

import auth.Identity;
import auth.InvalidLogin;
import domain.Right;
import domain.RightType;
import domain.User;
import lombok.Getter;
import lombok.Setter;
import persistence.base.Repository;
import persistence.base.constraints.Constraint;
import persistence.base.exceptions.CreationException;
import persistence.base.serialization.Field;
import persistence.drivers.inmemory.InMemoryRepository;
import persistence.models.InMemoryRepositoryFactory;
import services.AuthService;
import services.SessionStatefulService;

public class Application {
    private final Executor executor;
    private final ApplicationInput appInput;
    private final ApplicationOutput appOutput;
    private final SessionStatefulService sessionService;

    public Application(ApplicationInput appInput, ApplicationOutput appOutput) {
        this.appInput = appInput;
        this.appOutput = appOutput;

        var repoFactory = new InMemoryRepositoryFactory();
        Repository<User> userRepo = repoFactory.build("User");
        Repository<Right> rightRepo = repoFactory.build("Right");
        var authService = new AuthService(userRepo, rightRepo);
        sessionService = new SessionStatefulService(authService);

        try {
            userRepo.addConstraint(Constraint.UNIQUE, new Field("name"));
            userRepo.addConstraint(Constraint.UNIQUE, new Field("email"));

            userRepo.create(new User("admin", "admin", "admin@email.com"));
            rightRepo.create(new Right(RightType.MANAGE_BOOKS, "admin"));
            rightRepo.create(new Right(RightType.MANAGE_USERS, "admin"));
        } catch (Exception ignored) {

        }

        this.executor = new Executor(new Command[]{
            new Command(
                "/login", "<user> <password>",
                "Creates a new session.",
                (String[] args) -> {
                    var user = args[0];
                    var password = args[1];

                    try {
                        sessionService.login(user, password);
                    } catch (InvalidLogin ex) {
                        appOutput.writeLine("Invalid login: " + ex.getMessage());
                    }
                    return CommandStatus.SUCCESS;
                }, null),

            new Command("/logout", "",
                "Deletes the current session.",
                (String[] args) -> {
                    sessionService.logout();
                    return CommandStatus.SUCCESS;
                }, new RightType[]{}),

            new Command("/register", "<user> <password> <email>",
                "Registers a new reader user.",
                (String[] args) -> {
                    var user = args[0];
                    var password = args[1];
                    var email = args[2];

                    try {
                        authService.register(user, password, email);
                    } catch (CreationException ex) {
                        System.out.println("Couldn't register user: " + ex.getMessage());
                    }
                    return CommandStatus.SUCCESS;
                }, null),

            new Command("/books/add", "",
                "Adds a new book to the library.",
                (String[] args) -> {
                    return CommandStatus.SUCCESS;
                }, new RightType[]{RightType.MANAGE_BOOKS})
        }, this.appOutput, this.sessionService);
    }

    public void run() {
        appOutput.writeLine("--- Library management ---");

        String inputLine;
        while (true) {
            try {
                if (sessionService.isLoggedIn()) {
                    appOutput.writeLine("Logged in as " + sessionService.getCurrentUser().getName() + ".");
                }
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
