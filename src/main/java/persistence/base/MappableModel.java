package persistence.base;

public interface MappableModel<T> extends Model<T>, Mappable<T> {
    T getData();
}
