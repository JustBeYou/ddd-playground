package persistence.base.models;

import persistence.base.MappableModel;
import persistence.base.exceptions.UnknownModelException;

public interface ModelFactory {
  <T> MappableModel<T> build(String modelName, T data) throws UnknownModelException;
}
