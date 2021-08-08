package persistence.base.exceptions;

public class NullStorageReferenceException extends Exception {
  public NullStorageReferenceException(String modelName) {
    super("Entity of type " + modelName + " has no associated record (id is set to null).");
  }
}
