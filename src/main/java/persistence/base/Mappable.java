package persistence.base;

public interface Mappable<T> {
    FieldsMap map();
    T unmap(FieldsMap map);
    T unmapIfSet(T existingData, FieldsMap map);
}
