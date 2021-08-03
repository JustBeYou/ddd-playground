package persistence.base;

import persistence.base.exceptions.InvalidStorageReferenceException;
import persistence.base.exceptions.UnknownModelException;

import java.util.Collection;
import java.util.Optional;

public interface Repository<T> {
    MappableModel<T> create(T model) throws UnknownModelException;
    void delete(MappableModel<T> model) throws InvalidStorageReferenceException;
    Collection<MappableModel<T>> find(Query query);
    Optional<MappableModel<T>> findOne(Query query);
    Optional<MappableModel<T>> findById(Integer id) throws UnknownModelException;
    void update(MappableModel<T> model, FieldsMap values) throws InvalidStorageReferenceException;
}
