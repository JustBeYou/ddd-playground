package persistence.base.models;

import persistence.base.MappableModel;
import persistence.base.Repository;
import persistence.base.exceptions.UnknownModelException;

public interface ModelsFactoryDeprecated {
  <T> MappableModel<T> build(String modelName, T data) throws UnknownModelException;
}
