package domain.exceptions;

public class InvalidISBN extends Exception {
    public InvalidISBN(String isbn) {
        super(isbn + " is not a valid ISBN-13.");
    }
}
