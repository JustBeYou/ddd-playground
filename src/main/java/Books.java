import di.DIManager;
import domain.Book;
import models.inmemory.InMemoryRepository;

public class Books {
    public static void main(String[] args) {
        registerDeps();

        var book1 = new Book("Cel mai iubit dintre pamanteni", "1234", "1970");
        System.out.println(book1.toString());

        var book2 = new Book("Cartea Junglei", "4321", "1950");
        System.out.println(book2.toString());
    }

    private static void registerDeps() {
        var diManager = DIManager.getInstance();
        diManager.register("BookRepository", new InMemoryRepository<Book>());
    }
}