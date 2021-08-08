package persistence.base.exceptions;

public class UnknownModelException extends Exception {
  public UnknownModelException(String modelName) {
    super("Unknown model: " + modelName + ".");
  }
}
