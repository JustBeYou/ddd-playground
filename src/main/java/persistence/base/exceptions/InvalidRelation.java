package persistence.base.exceptions;

public class InvalidRelation extends Exception {
    public InvalidRelation(String model, String field) {
        super("The referenced entity by field: " + field + "on model: " + model + " does not exist.");
    }
}
