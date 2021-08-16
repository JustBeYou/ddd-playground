package services.csv;

public class InvalidRowLength extends Exception {
    public InvalidRowLength() {
        super("Row length should match header length.");
    }
}
