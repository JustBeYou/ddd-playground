package persistence.inmemory;

import lombok.NonNull;
import persistence.base.*;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class InMemoryRepository<T> implements Repository<T> {
    private Map<Integer, T> store;

    @Override
    public MappableModel<T> create(FieldsMap data) {
        data.getMap().remove("id");
        // TODO: add a factory for creating models

        return null;
    }

    @Override
    public void delete(MappableModel<T> model) {

    }

    @Override
    public Collection<MappableModel<T>> find(Query query) {
        return null;
    }

    @Override
    public Optional<T> findOne(Query query) {
        return Optional.empty();
    }

    @Override
    public Optional<T> findById(Integer id) {
        return Optional.empty();
    }

    @Override
    public void update(MappableModel<T> model, FieldsMap values) {

    }
}
