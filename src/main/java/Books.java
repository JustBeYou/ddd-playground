import di.DIManager;
import domain.Book;
import persistence.inmemory.InMemoryRepository;
import persistence.models.BookModel;

public class Books {
    public static void main(String[] args) {
        registerDeps();

        var book1 = new Book("Cel mai iubit dintre pamanteni", "1234", "1970");
        var bookModel1 = new BookModel(book1);
        System.out.println(book1.toString());
        System.out.println(bookModel1.toString());

        var book2 = new Book("Cartea Junglei", "4321", "1950");
        var bookModel2 = new BookModel(book2);
        System.out.println(book2.toString());
        System.out.println(bookModel2.toString());
    }

    private static void registerDeps() {
        var diManager = DIManager.getInstance();
        diManager.register("BookRepository", new InMemoryRepository<Book>());
    }
}