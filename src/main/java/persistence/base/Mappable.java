package persistence.base;

public interface Mappable<T> {
    FieldsMap map();
    T unmap(FieldsMap map);
}
