package persistence.base;

import persistence.base.constraints.Constraint;
import persistence.base.exceptions.*;
import persistence.base.queries.Query;
import persistence.base.serialization.Field;
import persistence.base.serialization.FieldsMap;

import java.util.Collection;
import java.util.Optional;

public interface Repository<T> {
    MappableModel<T> create(T model) throws CreationException;

    void delete(MappableModel<T> model) throws InvalidStorageReferenceException;

    Collection<MappableModel<T>> find(Query query) throws InvalidQueryOperation;

    Optional<MappableModel<T>> findOne(Query query);

    Optional<MappableModel<T>> findById(Integer id);

    boolean exists(Query query);

    void update(MappableModel<T> model, FieldsMap values) throws InvalidStorageReferenceException, UpdateException;

    void loadRelations(MappableModel<T> model);

    void addConstraint(Constraint constraint, Field field);

    default void save(MappableModel<T> model) throws CreationException, InvalidStorageReferenceException, UpdateException {
        if (model.getId() == null) {
            var newModel = this.create(model.getData());
            model.setId(newModel.getId());
        } else {
            this.update(model, model.map());
        }
    }

    default void reload(MappableModel<T> model) throws NullStorageReferenceException, InvalidStorageReferenceException {
        if (model.getId() == null) {
            throw new NullStorageReferenceException(model.getName());
        }

        var newModel = this.findById(model.getId());
        if (newModel.isEmpty()) {
            throw new InvalidStorageReferenceException(model.getName(), model.getId());
        }

        model.setData(newModel.get().getData());
    }
}
