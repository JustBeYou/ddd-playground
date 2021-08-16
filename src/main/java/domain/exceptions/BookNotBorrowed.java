package domain.exceptions;

public class BookNotBorrowed extends Exception {
    public BookNotBorrowed(String name) {
        super(name + " is not borrowed.");
    }
}
