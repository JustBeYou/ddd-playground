package persistence.drivers.inmemory;

import persistence.base.MappableModel;
import persistence.base.Repository;
import persistence.base.exceptions.InvalidQueryOperation;

public interface RuntimeConstraint<T> {
  boolean isSatisfied(MappableModel<T> model, Repository<T> repository) throws InvalidQueryOperation;
  String getFailingMessage();
}
