package models.inmemory;

import models.*;

import java.util.Collection;
import java.util.Optional;

public class InMemoryRepository<T extends Mappable<T>> implements Repository<T> {

    @Override
    public Model<T> create(ModelMap data) {
        return null;
    }

    @Override
    public void delete(Model<T> model) {

    }

    @Override
    public Collection<Model<T>> find(Query query) {
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
    public void update(Model<T> model, ModelMap values) {

    }
}
