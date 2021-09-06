package ui.modules;

import domain.*;
import domain.exceptions.BookNotAvailable;
import domain.exceptions.BookNotBorrowed;
import domain.exceptions.NotEnoughRights;
import lombok.Getter;
import lombok.NonNull;
import persistence.base.Repository;
import persistence.base.exceptions.CreationException;
import persistence.base.exceptions.InvalidQueryOperation;
import persistence.base.queries.*;
import services.SessionStatefulService;
import ui.ApplicationOutput;
import ui.Command;
import ui.CommandStatus;
import ui.Module;

import java.util.ArrayList;

public class BooksModule implements Module {
    @NonNull
    @Getter
    private final ArrayList<Command> commands;

    public BooksModule(
        ApplicationOutput appOutput,
        SessionStatefulService sessionService,
        Repository<Author> authorRepository,
        Repository<Book> bookRepository,
        Repository<Shelve> shelveRepository
    ) {
        commands = new ArrayList<>();

        commands.add(new Command("/books/add",
            "<name> <ISBN> <publishedAt> <authorName> <shelve> <pages>",
            "Adds a new book to the library.",
            (String[] args) -> {
                var name = args[0];
                var isbn = args[1];
                var publishedAt = args[2];
                var authorName = args[3];
                var shelve = args[4];
                var pages = Integer.valueOf(args[5]);

                var query = new QueryFactory().buildSimpleQuery("name", authorName, QueryOperation.Like);
                if (!authorRepository.exists(query)) {
                    appOutput.writeLine("Author does not exist.");
                    return CommandStatus.FAIL;
                }

                try {
                    bookRepository.create(new Book(name, isbn, publishedAt, authorName, shelve, pages));
                } catch (Exception ex) {
                    appOutput.writeLine("Could not add new book: " + ex.getMessage());
                    return CommandStatus.FAIL;
                }
                return CommandStatus.SUCCESS;
            }, new RightType[]{RightType.MANAGE_BOOKS})
        );

        commands.add(new Command(
           "/books/search", "<string>",
            "Search a book by name, date or author",
            (String[] args) -> {
               var str = args[0];

               var queryFactory = new QueryFactory();
               var query = new Query(queryFactory.buildOr(
                   new QueryNode[]{
                       queryFactory.buildClause("name", str, QueryOperation.Like),
                       queryFactory.buildClause("publishedAt", str, QueryOperation.Like),
                       queryFactory.buildClause("authorName", str, QueryOperation.Like)
                   }
               ));
               try {
                   var books = bookRepository.find(query);
                   appOutput.writeLine("Found the following books:");
                   for (var book: books) {
                       bookRepository.loadRelations(book);
                       appOutput.writeLine(book.getData().toString());
                   }
               } catch (Exception ex) {
                   appOutput.writeLine("Something went wrong: " + ex.getMessage());
                   return CommandStatus.FAIL;
               }

               return CommandStatus.SUCCESS;
            }, new RightType[]{}
        ));

        commands.add(new Command(
           "/books/borrow", "<name>",
           "Borrow a book by name.",
           args -> {
               var bookName = args[0];
               var foundBook = bookRepository.findOne(
                   new QueryFactory().buildSimpleQuery(
                       "name", bookName, QueryOperation.IsSame
                   )
               );
               if (foundBook.isEmpty()) {
                   appOutput.writeLine("Book not found.");
                   return CommandStatus.FAIL;
               }

               var book = foundBook.get();
               try {
                   book.getData().borrowTo(sessionService.getCurrentUser());
                   bookRepository.save(book);
               } catch (NotEnoughRights notEnoughRights) {
                   appOutput.writeLine("You are not allowed to borrow books.");
                   return CommandStatus.FAIL;
               } catch (BookNotAvailable bookNotAvailable) {
                    appOutput.writeLine("The book is not available for borrowing.");
                    return CommandStatus.FAIL;
               } catch (Exception ex) {
                   appOutput.writeLine("Couldn't borrow book" + ex.getMessage());
                   return CommandStatus.FAIL;
               }

               appOutput.writeLine("You borrowed " + bookName);
               return CommandStatus.SUCCESS;
           }, new RightType[]{RightType.BORROW_BOOKS}
        ));

        commands.add(new Command(
            "/books/return", "<name>",
            "Return a book specified by name.",
            args -> {
                var bookName = args[0];
                var foundBook = bookRepository.findOne(
                    new QueryFactory().buildSimpleQuery(
                        "name", bookName, QueryOperation.IsSame
                    )
                );
                if (foundBook.isEmpty()) {
                    appOutput.writeLine("Book not found.");
                    return CommandStatus.FAIL;
                }

                var book = foundBook.get();
                if (!sessionService.getCurrentUser().getName().equals(book.getData().getBorrowerName())) {
                    appOutput.writeLine("You don't own this book.");
                    return CommandStatus.FAIL;
                }

                try {
                    book.getData().returnIt();
                    bookRepository.save(book);
                } catch (BookNotBorrowed bookNotBorrowed) {
                    appOutput.writeLine("The book is not borrowed.");
                    return CommandStatus.FAIL;
                } catch (Exception ex) {
                    appOutput.writeLine("Couldn't return book: " + ex.getMessage());
                    return CommandStatus.FAIL;
                }

                appOutput.writeLine("You returned " + bookName);
                return CommandStatus.SUCCESS;
            }, new RightType[]{RightType.BORROW_BOOKS}
        ));

        commands.add(new Command(
            "/books/shelve/add", "<name>",
            "Creates a new shelve.",
            args -> {
                var name = args[0];

                try {
                    shelveRepository.create(new Shelve(name));
                } catch (CreationException e) {
                    appOutput.writeLine("Couldn't create shelve: " + e.getMessage());
                    return CommandStatus.FAIL;
                }
                appOutput.writeLine("Created new shelve " + name);

                return CommandStatus.SUCCESS;
            }, new RightType[]{RightType.MANAGE_BOOKS}
        ));

        commands.add(new Command(
            "/books/shelve/list_books", "<shelve>",
            "Lists all books from a shelve.",
            args -> {
                var name = args[0];
                var query = new QueryFactory().buildSimpleQuery(
                  "name", name, QueryOperation.IsSame
                );
                var foundShelve = shelveRepository.findOne(query);
                if (foundShelve.isEmpty()) {
                    appOutput.writeLine("Shelve not found.");
                    return CommandStatus.FAIL;
                }

                var shelve = foundShelve.get();
                try {
                    shelveRepository.loadRelations(shelve);
                } catch (InvalidQueryOperation ignored) {

                }

                if (shelve.getData().getBooks().isEmpty()) {
                    appOutput.writeLine("The shelve is empty.");
                } else {
                    appOutput.writeLine("Books in " + name + ":");
                    for (var book: shelve.getData().getBooks()) {
                        appOutput.writeLine(book.toString());
                    }
                }

                return CommandStatus.SUCCESS;
            }, new RightType[]{}
        ));

        commands.add(new Command(
            "/books/shelve/move_book", "<shelve> <book>",
            "Moves a book to a shelve.",
            args -> {
                var shelveName = args[0];
                var bookName = args[1];

                var query = new QueryFactory().buildSimpleQuery(
                    "name", shelveName, QueryOperation.IsSame
                );
                var foundShelve = shelveRepository.findOne(query);
                if (foundShelve.isEmpty()) {
                    appOutput.writeLine("Shelve not found.");
                    return CommandStatus.FAIL;
                }

                query = new QueryFactory().buildSimpleQuery(
                    "name", bookName, QueryOperation.IsSame
                );
                var foundBook = bookRepository.findOne(query);
                if (foundBook.isEmpty()) {
                    appOutput.writeLine("Book not found.");
                    return CommandStatus.FAIL;
                }

                var shelve = foundShelve.get();
                var book = foundBook.get();

                book.getData().setShelveName(shelve.getData().getName());
                try {
                    bookRepository.save(book);
                } catch (Exception ex) {
                    appOutput.writeLine("Could not move the book: " + ex.getMessage());
                    return CommandStatus.FAIL;
                }

                return CommandStatus.SUCCESS;
            }, new RightType[]{RightType.MANAGE_BOOKS}
        ));

        commands.add(new Command(
            "/books/shelve/list", "",
            "Lists shelves.",
            args -> {
                try {
                    var shelves = shelveRepository.find(new Query());
                    for (var shelve: shelves) {
                        appOutput.writeLine(shelve.getData().toString());
                    }
                } catch (InvalidQueryOperation ignored) {
                    return CommandStatus.FAIL;
                }

                return CommandStatus.SUCCESS;
            }, new RightType[]{}
        ));
    }
}
