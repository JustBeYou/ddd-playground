package models;

public interface Model<T> {
    Integer getId();
    void save();
    void delete();
    void reload() throws Exception;
}
