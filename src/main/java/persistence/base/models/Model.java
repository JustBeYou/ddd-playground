package persistence.base.models;

public interface Model<T> {
    String getName();

    Integer getId();

    void setId(Integer id);

    T getData();

    void setData(T data);
}
