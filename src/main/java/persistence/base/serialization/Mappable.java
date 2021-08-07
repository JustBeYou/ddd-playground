package persistence.base.serialization;

public interface Mappable<T> {
    FieldsMap map();
    T unmap(FieldsMap map);
    T unmapIfSet(T existingData, FieldsMap map);
}
