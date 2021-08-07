import di.DIManager;
import domain.Book;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import persistence.base.*;
import persistence.base.exceptions.InvalidQueryOperation;
import persistence.base.exceptions.InvalidStorageReferenceException;
import persistence.base.exceptions.NullStorageReferenceException;
import persistence.base.exceptions.UnknownModelException;
import persistence.inmemory.InMemoryRepository;
import persistence.models.BookModel;
import persistence.models.BooksModelsFactory;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryPersistenceTest {
  private Book createBook() {
    return new Book(
      "test",
      "123-123-123",
      "3 May 2015"
    );
  }

  private InMemoryRepository<Book> getRepo() {
    return DIManager.getInstance().get("BookRepository");
  }

  @BeforeAll
  static void setup() {
    var booksModelsFactory = new BooksModelsFactory();
    var diManager = DIManager.getInstance();
    diManager.register("BookRepository", new InMemoryRepository<Book>(booksModelsFactory));
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
    var bookModel = getRepo().create(book);
    assertNotNull(bookModel.getId());
    assertEquals(book.getName(), bookModel.getData().getName());
  }

  @Test
  void shouldFindEntityById() throws UnknownModelException {
    var bookModel = getRepo().create(createBook());
    var foundBookModel = getRepo().findById(bookModel.getId());
    assertTrue(foundBookModel.isPresent());
    assertEquals(foundBookModel.get().getData().getName(), bookModel.getData().getName());
  }

  @Test
  void shouldUpdateEntity() throws UnknownModelException, InvalidStorageReferenceException, NullStorageReferenceException {
    var bookModel = getRepo().create(createBook());
    bookModel.getData().setName("FAKE");
    bookModel.save();
    var foundBookModel = getRepo().findById(bookModel.getId());
    assertTrue(foundBookModel.isPresent());
    assertEquals(foundBookModel.get().getData().getName(), "FAKE");
  }

  @Test
  void shouldDeleteEntity() throws UnknownModelException, NullStorageReferenceException, InvalidStorageReferenceException {
    var bookModel = getRepo().create(createBook());
    bookModel.delete();
    var foundBookModel = getRepo().findById(bookModel.getId());
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
      getRepo().create(new Book("fake01", "0002", "2012")).getId(),
      getRepo().create(new Book("fake02", "0002", "2012")).getId(),
      getRepo().create(new Book("fake03", "0002", "2012")).getId(),
      getRepo().create(new Book("fake04", "0001", "2012")).getId(),
      getRepo().create(new Book("fake05", "0001", "2012")).getId(),
    };

    var result = getRepo().find(query);
    assertEquals(3, result.size());

    var foundIds = result.stream().map(Model::getId).collect(Collectors.toList());
    assertTrue(foundIds.contains(ids[0]));
    assertTrue(foundIds.contains(ids[3]));
    assertTrue(foundIds.contains(ids[4]));
  }
}
