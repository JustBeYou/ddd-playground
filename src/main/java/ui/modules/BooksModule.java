package ui.modules;

import domain.Author;
import domain.Book;
import domain.Country;
import domain.RightType;
import lombok.Getter;
import lombok.NonNull;
import persistence.base.Repository;
import persistence.base.queries.Query;
import persistence.base.queries.QueryFactory;
import persistence.base.queries.QueryNode;
import persistence.base.queries.QueryOperation;
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
    }
}
