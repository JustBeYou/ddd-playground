package domain.exceptions;

public class BookNotAvailable extends Exception {
    public BookNotAvailable(String name) {
        super(name + " is not available to be borrowed/requested for read.");
    }
}
