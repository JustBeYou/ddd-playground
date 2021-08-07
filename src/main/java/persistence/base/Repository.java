package persistence.base;

import persistence.base.exceptions.InvalidQueryOperation;
import persistence.base.exceptions.InvalidStorageReferenceException;
import persistence.base.exceptions.UnknownModelException;
import persistence.base.queries.Query;
import persistence.base.serialization.FieldsMap;

import java.util.Collection;
import java.util.Optional;

public interface Repository<T> {
    MappableModel<T> create(T model) throws UnknownModelException;
    void delete(MappableModel<T> model) throws InvalidStorageReferenceException;
    Collection<MappableModel<T>> find(Query query) throws UnknownModelException, InvalidQueryOperation;
    Optional<MappableModel<T>> findOne(Query query) throws UnknownModelException;
    Optional<MappableModel<T>> findById(Integer id) throws UnknownModelException;
    void update(MappableModel<T> model, FieldsMap values) throws InvalidStorageReferenceException;
    void loadRelations(MappableModel<T> model) throws UnknownModelException;
}
