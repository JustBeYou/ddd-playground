package domain.exceptions;

public class InvalidRating extends Exception {
    public InvalidRating() {
        super("Rating should be between 1 - 5");
    }
}
