package ui;

import domain.*;
import persistence.base.Repository;
import persistence.base.RepositoryFactory;
import persistence.base.constraints.Constraint;
import persistence.base.serialization.Field;
import services.AuthService;
import services.LoggerService;
import services.SessionStatefulService;
import ui.modules.AdminModule;
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
        RepositoryFactory repoFactory,
        LoggerService loggerService
    ) {
        this.appInput = appInput;
        this.appOutput = appOutput;

        Repository<User> userRepo = (Repository<User>) repoFactory.build("User");
        Repository<Right> rightRepo = (Repository<Right>) repoFactory.build("Right");
        Repository<Author> authorRepo = (Repository<Author>) repoFactory.build("Author");
        Repository<Book> bookRepo = (Repository<Book>) repoFactory.build("Book");
        Repository<Shelve> shelveRepo = (Repository<Shelve>) repoFactory.build("Shelve");
        Repository<ReadingTracker> readingTrackerRepo = (Repository<ReadingTracker>) repoFactory.build("ReadingTracker");
        var authService = new AuthService(userRepo, rightRepo);
        sessionService = new SessionStatefulService(authService);

        try {
            userRepo.addConstraint(Constraint.UNIQUE, new Field("name"));
            userRepo.addConstraint(Constraint.UNIQUE, new Field("email"));
            bookRepo.addConstraint(Constraint.UNIQUE, new Field("name"));
            authorRepo.addConstraint(Constraint.UNIQUE, new Field("name"));
            shelveRepo.addConstraint(Constraint.UNIQUE, new Field("name"));

            userRepo.create(new User("admin", "admin", "admin@email.com"));
            shelveRepo.create(new Shelve("default-shelve"));
            authorRepo.create(new Author("default-author", Country.Romania));
            bookRepo.create(new Book("default-book", "9784607084182", "13-May-1990", "default-author", "default-shelve", 100));
            rightRepo.create(new Right(RightType.MANAGE_BOOKS, "admin"));
            rightRepo.create(new Right(RightType.MANAGE_USERS, "admin"));
            rightRepo.create(new Right(RightType.BORROW_BOOKS, "admin"));
        } catch (Exception ignored) {
        }

        this.executor = new Executor(appOutput, sessionService, loggerService);
        this.executor.registerModule(
            new AuthModule(appOutput, sessionService, authService)
        );
        this.executor.registerModule(
            new BooksModule(appOutput, sessionService, authorRepo, bookRepo, shelveRepo, readingTrackerRepo)
        );
        this.executor.registerModule(
            new AdminModule(appOutput, userRepo, rightRepo)
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
