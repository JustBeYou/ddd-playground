package persistence.base.serialization;

import java.util.Collection;

public interface Mappable<T> {
    FieldsMap map();

    T unmap(FieldsMap map);

    T unmapIfSet(T existingData, FieldsMap map);

    Collection<Field> getFields();
}
