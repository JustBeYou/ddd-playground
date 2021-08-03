package persistence.base;

public interface Model<T> {
    String getName();
    Integer getId();
    void setId(Integer id);
    T getData();
    void setData(T data);
    Repository<T> getAssociatedRepository();
}
