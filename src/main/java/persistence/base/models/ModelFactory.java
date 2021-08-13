package persistence.base.models;

import persistence.base.MappableModel;

public interface ModelFactory<T> {
    String getModelName();

    MappableModel<T> build(T data);
}
