package persistence.base.models;

import persistence.base.MappableModel;

public interface ModelsFactoryDeprecated {
    <T> MappableModel<T> build(String modelName, T data);
}
