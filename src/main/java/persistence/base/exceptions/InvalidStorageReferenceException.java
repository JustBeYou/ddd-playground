package persistence.base.exceptions;

public class InvalidStorageReferenceException extends Exception {
  public InvalidStorageReferenceException(String modelName, Integer id) {
    super("Entity of type " + modelName + " with id " + id + " is no longer present in storage.");
  }
}
