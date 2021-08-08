package persistence.base.models;

import persistence.base.MappableModel;
import persistence.base.Repository;

public interface ModelsFactoryDeprecated {
  <T> MappableModel<T> build(String modelName, T data);
}
