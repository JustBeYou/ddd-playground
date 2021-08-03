package persistence.inmemory;

import lombok.Data;
import persistence.base.*;
import persistence.base.exceptions.InvalidStorageReferenceException;
import persistence.base.exceptions.UnknownModelException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryRepository<T> implements Repository<T> {
    private Integer nextId;
    private final Map<Integer, T> store;
    private final ModelFactory modelFactory;

    public InMemoryRepository(ModelFactory modelFactory) {
        this.modelFactory = modelFactory;
        this.store = new HashMap<>();
        this.nextId = 1;
    }

    @Override
    public MappableModel<T> create(T data) throws UnknownModelException {
        var className = data.getClass().getSimpleName();
        MappableModel<T> model = this.modelFactory.build(
                className,
                data
        );

        model.setId(this.nextId++);
        this.store.put(model.getId(), model.getData());

        return model;
    }

    @Override
    public void delete(MappableModel<T> model) throws InvalidStorageReferenceException {
        if (this.store.containsKey(model.getId())) {
            this.store.remove(model.getId());
        } else {
            throw new InvalidStorageReferenceException(model.getName(), model.getId());
        }
    }

    @Override
    public Collection<MappableModel<T>> find(Query query) {
        return null;
    }

    @Override
    public Optional<MappableModel<T>> findOne(Query query) {
        return Optional.empty();
    }

    @Override
    public Optional<MappableModel<T>> findById(Integer id) throws UnknownModelException {
        if (this.store.containsKey(id)) {
            var data = this.store.get(id);
            var className = data.getClass().getSimpleName();
            var model = modelFactory.build(
                    className,
                    data
            );
            model.setId(id);
            return Optional.of(model);
        }
        return Optional.empty();
    }

    @Override
    public void update(MappableModel<T> model, FieldsMap values) throws InvalidStorageReferenceException {
        if (this.store.containsKey(model.getId())) {
            model.unmapIfSet(model.getData(), values);
            this.store.put(model.getId(), model.getData());
        } else {
            throw new InvalidStorageReferenceException(model.getName(), model.getId());
        }
    }
}
