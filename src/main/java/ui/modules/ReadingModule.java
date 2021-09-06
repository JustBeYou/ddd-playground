package ui.modules;

import domain.*;
import lombok.Getter;
import lombok.NonNull;
import persistence.base.Repository;
import persistence.base.exceptions.CreationException;
import persistence.base.queries.Query;
import persistence.base.queries.QueryFactory;
import persistence.base.queries.QueryNode;
import persistence.base.queries.QueryOperation;
import services.SessionStatefulService;
import ui.ApplicationOutput;
import ui.Command;
import ui.CommandStatus;
import ui.Module;

import java.util.ArrayList;

public class ReadingModule implements Module {
    @NonNull
    @Getter
    private final ArrayList<Command> commands;

    public ReadingModule(
        ApplicationOutput appOutput,
        SessionStatefulService sessionService,
        Repository<Book> bookRepository,
        Repository<ReadingTracker> readingTrackerRepository
    ) {
        commands = new ArrayList<>();

        commands.add(new Command(
            "/books/to_read/add", "<book>",
            "Add a book to the reading list",
            args -> {
                var bookName = args[0];

                var query = new QueryFactory().buildSimpleQuery(
                    "name", bookName, QueryOperation.IsSame
                );
                var foundBook = bookRepository.findOne(query);
                if (foundBook.isEmpty()) {
                    appOutput.writeLine("Book not found.");
                    return CommandStatus.FAIL;
                }

                query = new QueryFactory().buildSimpleQuery(
                    "bookName", bookName, QueryOperation.IsSame
                );
                var alreadyExists = readingTrackerRepository.exists(query);
                if (alreadyExists) {
                    appOutput.writeLine("Book already in the reading list.");
                    return CommandStatus.FAIL;
                }

                try {
                    readingTrackerRepository.create(
                        new ReadingTracker(sessionService.getCurrentUser().getName(), bookName)
                    );
                } catch (CreationException e) {
                    appOutput.writeLine("Could not add book to reading list: " + e.getMessage());
                    return CommandStatus.FAIL;
                }

                appOutput.writeLine("Added book to reading list.");

                return CommandStatus.SUCCESS;
            }, new RightType[]{}
        ));

        commands.add(new Command(
            "/books/to_read/list", "",
            "List all book in to-read list.",
            args -> {
                var queryFactory = new QueryFactory();
                var node = queryFactory.buildAnd(
                    new QueryNode[]{
                        queryFactory.buildClause("started", "", QueryOperation.IsFalse),
                        queryFactory.buildClause("userName", sessionService.getCurrentUser().getName(), QueryOperation.IsSame)
                    }
                );
                var query = new Query(node);

                appOutput.writeLine("Books in to-read list:");

                try {
                    var trackers = readingTrackerRepository.find(query);
                    for (var tracker: trackers) {
                        readingTrackerRepository.loadRelations(tracker);
                        appOutput.writeLine(tracker.getData().getBook().toString());
                    }
                } catch (Exception e) {
                    appOutput.writeLine("Could not list books: " + e.getMessage());
                    return CommandStatus.FAIL;
                }

                return CommandStatus.SUCCESS;
            }, new RightType[]{}
        ));

        commands.add(new Command(
            "/books/read_pages", "<book> <pages>",
            "Read an amount of pages.",
            args -> {
                var bookName = args[0];
                var pages = Integer.valueOf(args[1]);

                var query = new QueryFactory().buildSimpleQuery(
                    "bookName", bookName, QueryOperation.IsSame
                );
                var foundTracker = readingTrackerRepository.findOne(query);
                if (foundTracker.isEmpty()) {
                    appOutput.writeLine("Book not in your reading list.");
                    return CommandStatus.FAIL;
                }

                var tracker = foundTracker.get();
                try {
                    readingTrackerRepository.loadRelations(tracker);
                    tracker.getData().readPages(pages);
                    readingTrackerRepository.save(tracker);
                } catch (Exception exception) {
                    appOutput.writeLine("Couldn't save read pages");
                    return CommandStatus.FAIL;
                }

                appOutput.writeLine("Successfully updated reading status.");
                return CommandStatus.SUCCESS;
            }, new RightType[]{}
        ));

        commands.add(new Command(
            "/books/reading/list", "",
            "List books you are currently reading.",
            args -> {
                var queryFactory = new QueryFactory();
                var node = queryFactory.buildAnd(
                    new QueryNode[]{
                        queryFactory.buildClause("started", "", QueryOperation.IsTrue),
                        queryFactory.buildClause("finished", "", QueryOperation.IsFalse),
                        queryFactory.buildClause("userName", sessionService.getCurrentUser().getName(), QueryOperation.IsSame)
                    }
                );
                var query = new Query(node);

                appOutput.writeLine("Books in reading list:");

                try {
                    var trackers = readingTrackerRepository.find(query);
                    for (var tracker: trackers) {
                        readingTrackerRepository.loadRelations(tracker);
                        appOutput.writeLine(tracker.getData().getBook().toString() + " " + tracker.getData().getPage() +
                            "/" + tracker.getData().getBook().getPages());
                    }
                } catch (Exception e) {
                    appOutput.writeLine("Could not list books: " + e.getMessage());
                    return CommandStatus.FAIL;
                }

                return CommandStatus.SUCCESS;
            }, new RightType[]{}
        ));

        commands.add(new Command(
            "/books/read/list", "",
            "List books you are currently reading.",
            args -> {
                var queryFactory = new QueryFactory();
                var node = queryFactory.buildAnd(
                    new QueryNode[]{
                        queryFactory.buildClause("finished", "", QueryOperation.IsTrue),
                        queryFactory.buildClause("userName", sessionService.getCurrentUser().getName(), QueryOperation.IsSame)
                    }
                );
                var query = new Query(node);

                appOutput.writeLine("Books in read list:");

                try {
                    var trackers = readingTrackerRepository.find(query);
                    for (var tracker: trackers) {
                        readingTrackerRepository.loadRelations(tracker);
                        appOutput.writeLine(tracker.getData().getBook().toString());
                    }
                } catch (Exception e) {
                    appOutput.writeLine("Could not list books: " + e.getMessage());
                    return CommandStatus.FAIL;
                }

                return CommandStatus.SUCCESS;
            }, new RightType[]{}
        ));
    }
}
