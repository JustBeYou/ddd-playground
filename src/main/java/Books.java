import di.DIManager;
import domain.Author;
import domain.Book;
import domain.Country;
import persistence.base.Repository;
import persistence.base.exceptions.InvalidStorageReferenceException;
import persistence.base.exceptions.NullStorageReferenceException;
import persistence.base.exceptions.UnknownModelException;
import persistence.drivers.inmemory.InMemoryRepository;
import persistence.models.AuthorModel;
import persistence.models.BookModel;
import persistence.models.BooksModelsFactory;

public class Books {
  public static void main(String[] args) throws UnknownModelException, InvalidStorageReferenceException, NullStorageReferenceException {
    registerDeps();

    var author = new Author("Misu", Country.Romania);
    var authorModel = new AuthorModel(author);
    authorModel.save();

    var book1 = new Book("Cel mai iubit dintre pamanteni", "1234", "1970", "Misu");
    var bookModel1 = new BookModel(book1);
    bookModel1.save();
    System.out.println(book1.toString());
    System.out.println(bookModel1.toString());

    var book2 = new Book("Cartea Junglei", "4321", "1950", "Misu");
    var bookModel2 = new BookModel(book2);
    bookModel2.save();
    System.out.println(book2.toString());
    System.out.println(bookModel2.toString());

    Repository<Book> bookRepository = DIManager.getInstance().get("BookRepository");
    var bookModel3 = bookRepository.create(book2);
    System.out.println(bookModel3.toString());

    var foundBook = bookRepository.findById(2);
    var book = foundBook.orElse(null);
    System.out.println(book);

    book.getData().setName("HACKED BOOK!");
    book.save();
    book.reload();

    System.out.println(book);
    var foundBook12 = bookRepository.findById(2);
    var book12 = foundBook12.orElse(null);
    System.out.println(book12);

    System.out.println("-------------------------");
    authorModel.loadRelations();
    for (var currBook : authorModel.getData().getBooks()) {
      System.out.println(currBook.toString());
    }
  }

  private static void registerDeps() {
    var booksModelsFactory = new BooksModelsFactory();

    var diManager = DIManager.getInstance();
    diManager.register("BookRepository", new InMemoryRepository<Book>("Book", booksModelsFactory));
    diManager.register("AuthorRepository", new InMemoryRepository<Author>("Author", booksModelsFactory));
  }
}