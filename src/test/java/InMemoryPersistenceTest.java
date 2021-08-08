import di.DIManager;
import domain.Author;
import domain.Book;
import domain.Country;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import persistence.base.exceptions.InvalidStorageReferenceException;
import persistence.base.exceptions.NullStorageReferenceException;
import persistence.base.exceptions.UnknownModelException;
import persistence.base.models.Model;
import persistence.base.queries.Query;
import persistence.base.queries.QueryNode;
import persistence.base.queries.QueryNodeFactory;
import persistence.base.queries.QueryOperation;
import persistence.base.serialization.Field;
import persistence.base.serialization.FieldType;
import persistence.inmemory.InMemoryRepository;
import persistence.models.BookModel;
import persistence.models.BooksModelsFactory;

import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryPersistenceTest {
  private Book createBook() {
    return new Book(
      "test",
      "123-123-123",
      "3 May 2015",
      "Misu"
    );
  }

  private InMemoryRepository<Book> getBookRepo() {
    return DIManager.getInstance().get("BookRepository");
  }

  private InMemoryRepository<Author> getAuthorRepo() {
    return DIManager.getInstance().get("AuthorRepository");
  }

  @BeforeAll
  static void setup() throws UnknownModelException {
    var booksModelsFactory = new BooksModelsFactory();
    var diManager = DIManager.getInstance();
    var bookRepo = new InMemoryRepository<Book>("Book", booksModelsFactory);
    var authorRepo = new InMemoryRepository<Author>("Author", booksModelsFactory);
    diManager.register("BookRepository", bookRepo);
    diManager.register("AuthorRepository", authorRepo);

    var author = new Author("Misu", Country.Romania);
    authorRepo.create(author);
  }

  @Test
  void shouldAssignId() throws UnknownModelException, InvalidStorageReferenceException {
    var bookModel = new BookModel(createBook());
    assertNull(bookModel.getId());
    bookModel.save();
    assertNotNull(bookModel.getId());
  }

  @Test
  void shouldCreateEntity() throws UnknownModelException {
    var book = createBook();
    var bookModel = getBookRepo().create(book);
    assertNotNull(bookModel.getId());
    assertEquals(book.getName(), bookModel.getData().getName());
  }

  @Test
  void shouldFindEntityById() throws UnknownModelException {
    var bookModel = getBookRepo().create(createBook());
    var foundBookModel = getBookRepo().findById(bookModel.getId());
    assertTrue(foundBookModel.isPresent());
    assertEquals(foundBookModel.get().getData().getName(), bookModel.getData().getName());
  }

  @Test
  void shouldUpdateEntity() throws UnknownModelException, InvalidStorageReferenceException, NullStorageReferenceException {
    var bookModel = getBookRepo().create(createBook());
    bookModel.getData().setName("FAKE");
    bookModel.save();
    var foundBookModel = getBookRepo().findById(bookModel.getId());
    assertTrue(foundBookModel.isPresent());
    assertEquals(foundBookModel.get().getData().getName(), "FAKE");
  }

  @Test
  void shouldDeleteEntity() throws UnknownModelException, NullStorageReferenceException, InvalidStorageReferenceException {
    var bookModel = getBookRepo().create(createBook());
    bookModel.delete();
    var foundBookModel = getBookRepo().findById(bookModel.getId());
    assertTrue(foundBookModel.isEmpty());
  }

  @Test
  void shouldFindEntityByQuery() throws UnknownModelException {
    var queryNodeFactory = new QueryNodeFactory();
    var query = new Query(queryNodeFactory.buildOr(new QueryNode[]{
      queryNodeFactory.buildClause(new Field("ISBN", "0001"), QueryOperation.Like),
      queryNodeFactory.buildClause(new Field("name", "fake01"), QueryOperation.Like),
    }));

    var ids = new Integer[]{
      getBookRepo().create(new Book("fake01", "0002", "2012", "Misu")).getId(),
      getBookRepo().create(new Book("fake02", "0002", "2012", "Misu")).getId(),
      getBookRepo().create(new Book("fake03", "0002", "2012", "Misu")).getId(),
      getBookRepo().create(new Book("fake04", "0001", "2012", "Misu")).getId(),
      getBookRepo().create(new Book("fake05", "0001", "2012", "Misu")).getId(),
    };

    var result = getBookRepo().find(query);
    assertEquals(3, result.size());

    var foundIds = result.stream().map(Model::getId).collect(Collectors.toList());
    assertTrue(foundIds.contains(ids[0]));
    assertTrue(foundIds.contains(ids[3]));
    assertTrue(foundIds.contains(ids[4]));
  }

  @Test
  void shouldLoadRelation() throws UnknownModelException, NullStorageReferenceException {
    var bookModel = getBookRepo().create(new Book(
      "relation_test",
      "123-123-123",
      "25 May",
      "Misu"
    ));
    bookModel.loadRelations();
    assertNotNull(bookModel.getData().getAuthor());
    assertEquals("Misu", bookModel.getData().getAuthor().getName());

    var queryNodeFactory = new QueryNodeFactory();
    var query = new Query(
      queryNodeFactory.buildClause(
        new Field("name", FieldType.String, "Misu"),
        QueryOperation.Like
      )
    );
    var foundAuthor = getAuthorRepo().findOne(query);
    assertTrue(foundAuthor.isPresent());
    var authorModel = foundAuthor.get();
    authorModel.loadRelations();
    assertTrue(authorModel.getData().getBooks().size() > 0);
    assertTrue(authorModel.getData().getBooks().stream()
      .map(Book::getName)
      .anyMatch(Predicate.isEqual("relation_test"))
    );
  }
}
