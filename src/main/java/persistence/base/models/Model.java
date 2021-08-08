package persistence.base.models;

import persistence.base.Repository;

public interface Model<T> {
  String getName();

  Integer getId();

  void setId(Integer id);

  T getData();

  void setData(T data);

  Repository<T> getAssociatedRepository();
}
