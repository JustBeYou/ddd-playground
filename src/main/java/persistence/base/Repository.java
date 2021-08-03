package persistence.base;

import java.util.Collection;
import java.util.Optional;

public interface Repository<T> {
    MappableModel<T> create(FieldsMap data);
    void delete(MappableModel<T> model);
    Collection<MappableModel<T>> find(Query query);
    Optional<T> findOne(Query query);
    Optional<T> findById(Integer id);
    void update(MappableModel<T> model, FieldsMap values);
}
