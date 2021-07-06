package models;

import java.util.Collection;
import java.util.Optional;

public interface Repository<T extends Mappable<T>> {
    Model<T> create(ModelMap data);
    void delete(Model<T> model);
    Collection<Model<T>> find(Query query);
    Optional<T> findOne(Query query);
    Optional<T> findById(Integer id);
    void update(Model<T> model, ModelMap values);
}
