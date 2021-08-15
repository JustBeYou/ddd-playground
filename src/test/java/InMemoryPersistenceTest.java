import domain.Author;
import domain.Book;
import domain.Country;
import domain.exceptions.InvalidISBN;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import persistence.base.Repository;
import persistence.base.constraints.Constraint;
import persistence.base.exceptions.CreationException;
import persistence.base.exceptions.InvalidQueryOperation;
import persistence.base.exceptions.InvalidStorageReferenceException;
import persistence.base.exceptions.UpdateException;
import persistence.base.models.Model;
import persistence.base.queries.Query;
import persistence.base.queries.QueryNode;
import persistence.base.queries.QueryFactory;
import persistence.base.queries.QueryOperation;
import persistence.base.serialization.Field;
import persistence.base.serialization.FieldType;
import persistence.models.InMemoryRepositoryFactory;
import persistence.drivers.inmemory.InMemoryRepository;
import persistence.models.AuthorModelFactory;
import persistence.models.BookModel;
import persistence.models.BookModelFactory;

import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryPersistenceTest {
    static Repository<Book> bookRepo;
    static Repository<Author> authorRepo;
    static int cnt = 0;

    @BeforeAll
    static void setup() throws CreationException {
        authorRepo = new InMemoryRepository<>(new AuthorModelFactory(), new InMemoryRepositoryFactory());
        bookRepo = new InMemoryRepository<>(new BookModelFactory(), new InMemoryRepositoryFactory());

        authorRepo.addConstraint(Constraint.UNIQUE, new Field("name"));
        bookRepo.addConstraint(Constraint.UNIQUE, new Field("name"));

        var author = new Author("Misu", Country.Romania);
        authorRepo.create(author);
    }

    private Book createBook() throws InvalidISBN {
        return new Book(
            "test" + cnt++,
            "9783161484100",
            "3 May 2015",
            "Misu"
        );
    }

    @Test
    void shouldAssignId() throws InvalidStorageReferenceException, CreationException, UpdateException, InvalidISBN {
        var bookModel = new BookModel(createBook());
        assertNull(bookModel.getId());
        bookRepo.save(bookModel);
        assertNotNull(bookModel.getId());
    }

    @Test
    void shouldCreateEntity() throws CreationException, InvalidISBN {
        var book = createBook();
        var bookModel = bookRepo.create(book);
        assertNotNull(bookModel.getId());
        assertEquals(book.getName(), bookModel.getData().getName());
    }

    @Test
    void shouldFindEntityById() throws CreationException, InvalidISBN {
        var bookModel = bookRepo.create(createBook());
        var foundBookModel = bookRepo.findById(bookModel.getId());
        assertTrue(foundBookModel.isPresent());
        assertEquals(foundBookModel.get().getData().getName(), bookModel.getData().getName());
    }

    @Test
    void shouldUpdateEntity() throws InvalidStorageReferenceException, CreationException, UpdateException, InvalidISBN {
        var bookModel = bookRepo.create(createBook());
        bookModel.getData().setName("FAKE");
        bookRepo.save(bookModel);
        var foundBookModel = bookRepo.findById(bookModel.getId());
        assertTrue(foundBookModel.isPresent());
        assertEquals(foundBookModel.get().getData().getName(), "FAKE");
    }

    @Test
    void shouldDeleteEntity() throws InvalidStorageReferenceException, CreationException, InvalidISBN {
        var bookModel = bookRepo.create(createBook());
        bookRepo.delete(bookModel);
        var foundBookModel = bookRepo.findById(bookModel.getId());
        assertTrue(foundBookModel.isEmpty());
    }

    @Test
    void shouldFindEntityByQuery() throws InvalidQueryOperation, CreationException, InvalidISBN {
        var queryNodeFactory = new QueryFactory();
        var query = new Query(queryNodeFactory.buildOr(new QueryNode[]{
            queryNodeFactory.buildClause(new Field("ISBN", "9788249976515"), QueryOperation.Like),
            queryNodeFactory.buildClause(new Field("name", "fake01"), QueryOperation.Like),
        }));

        var ids = new Integer[]{
            bookRepo.create(new Book("fake01", "9781160029544", "2012", "Misu")).getId(),
            bookRepo.create(new Book("fake02", "9781160029544", "2012", "Misu")).getId(),
            bookRepo.create(new Book("fake03", "9781160029544", "2012", "Misu")).getId(),
            bookRepo.create(new Book("fake04", "9788249976515", "2012", "Misu")).getId(),
            bookRepo.create(new Book("fake05", "9788249976515", "2012", "Misu")).getId(),
        };

        var result = bookRepo.find(query);
        assertEquals(3, result.size());

        var foundIds = result.stream().map(Model::getId).collect(Collectors.toList());
        assertTrue(foundIds.contains(ids[0]));
        assertTrue(foundIds.contains(ids[3]));
        assertTrue(foundIds.contains(ids[4]));
    }

    @Test
    void shouldLoadRelation() throws CreationException, InvalidISBN, InvalidQueryOperation {
        var bookModel = bookRepo.create(new Book(
            "relation_test",
            "9783161484100",
            "25 May",
            "Misu"
        ));
        bookRepo.loadRelations(bookModel);
        assertNotNull(bookModel.getData().getAuthor());
        assertEquals("Misu", bookModel.getData().getAuthor().getName());

        var queryNodeFactory = new QueryFactory();
        var query = new Query(
            queryNodeFactory.buildClause(
                new Field("name", FieldType.String, "Misu"),
                QueryOperation.Like
            )
        );
        var foundAuthor = authorRepo.findOne(query);
        assertTrue(foundAuthor.isPresent());
        var authorModel = foundAuthor.get();
        authorRepo.loadRelations(authorModel);
        assertTrue(authorModel.getData().getBooks().size() > 0);
        assertTrue(authorModel.getData().getBooks().stream()
            .map(Book::getName)
            .anyMatch(Predicate.isEqual("relation_test"))
        );
    }
}
