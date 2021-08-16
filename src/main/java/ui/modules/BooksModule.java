package ui.modules;

import domain.Author;
import domain.Book;
import domain.Country;
import domain.RightType;
import domain.exceptions.BookNotAvailable;
import domain.exceptions.BookNotBorrowed;
import domain.exceptions.NotEnoughRights;
import lombok.Getter;
import lombok.NonNull;
import persistence.base.Repository;
import persistence.base.exceptions.CreationException;
import persistence.base.exceptions.InvalidStorageReferenceException;
import persistence.base.exceptions.UpdateException;
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

public class BooksModule implements Module {
    @NonNull
    @Getter
    private final ArrayList<Command> commands;

    public BooksModule(
        ApplicationOutput appOutput,
        SessionStatefulService sessionService,
        Repository<Author> authorRepository,
        Repository<Book> bookRepository
    ) {
        commands = new ArrayList<>();

        commands.add(new Command("/books/add",
            "<name> <ISBN> <publishedAt> <authorName>",
            "Adds a new book to the library.",
            (String[] args) -> {
                var name = args[0];
                var isbn = args[1];
                var publishedAt = args[2];
                var authorName = args[3];

                var query = new QueryFactory().buildSimpleQuery("name", authorName, QueryOperation.Like);
                if (!authorRepository.exists(query)) {
                    appOutput.writeLine("Author does not exist.");
                    return CommandStatus.FAIL;
                }

                try {
                    bookRepository.create(new Book(name, isbn, publishedAt, authorName));
                } catch (Exception ex) {
                    appOutput.writeLine("Could not add new book: " + ex.getMessage());
                    return CommandStatus.FAIL;
                }
                return CommandStatus.SUCCESS;
            }, new RightType[]{RightType.MANAGE_BOOKS})
        );

        commands.add(new Command(
            "/authors/add", "<name> <country>",
            "Adds a new book author.",
            (String[] args) -> {
                var name = args[0];
                var country = args[1];

                try {
                    authorRepository.create(new Author(name, Country.valueOf(country)));
                } catch (Exception ex) {
                    appOutput.writeLine("Could not add new author: " + ex.getMessage());
                    return CommandStatus.FAIL;
                }

                return CommandStatus.SUCCESS;
            }, new RightType[]{RightType.MANAGE_BOOKS}
        ));

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
    }
}
