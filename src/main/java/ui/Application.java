package ui;

import domain.*;
import persistence.base.Repository;
import persistence.base.RepositoryFactory;
import persistence.base.constraints.Constraint;
import persistence.base.serialization.Field;
import persistence.models.InMemoryRepositoryFactory;
import services.AuthService;
import services.SessionStatefulService;
import ui.modules.AuthModule;
import ui.modules.BooksModule;

public class Application {
    private final Executor executor;
    private final ApplicationInput appInput;
    private final ApplicationOutput appOutput;
    private final SessionStatefulService sessionService;

    public Application(
        ApplicationInput appInput,
        ApplicationOutput appOutput,
        RepositoryFactory repoFactory
    ) {
        this.appInput = appInput;
        this.appOutput = appOutput;

        Repository<User> userRepo = (Repository<User>) repoFactory.build("User");
        Repository<Right> rightRepo = (Repository<Right>) repoFactory.build("Right");
        Repository<Author> authorRepo = (Repository<Author>) repoFactory.build("Author");
        Repository<Book> bookRepo = (Repository<Book>) repoFactory.build("Book");
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

        this.executor = new Executor(appOutput, sessionService);
        this.executor.registerModule(
            new AuthModule(appOutput, sessionService, authService)
        );
        this.executor.registerModule(
            new BooksModule(appOutput, authorRepo, bookRepo)
        );
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
