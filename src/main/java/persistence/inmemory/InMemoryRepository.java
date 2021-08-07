package persistence.inmemory;

import persistence.base.*;
import persistence.base.exceptions.InvalidQueryOperation;
import persistence.base.exceptions.InvalidStorageReferenceException;
import persistence.base.exceptions.UnknownModelException;
import persistence.base.models.ModelFactory;
import persistence.base.queries.*;
import persistence.base.serialization.Field;
import persistence.base.serialization.FieldType;
import persistence.base.serialization.FieldsMap;

import java.util.*;
import java.util.function.BiFunction;

public class InMemoryRepository<T> implements Repository<T> {
    private Integer nextId;
    private final Map<Integer, T> store;
    private final ModelFactory modelFactory;
    private final String modelName;

    public InMemoryRepository(String modelName, ModelFactory modelFactory) {
        this.modelName = modelName;
        this.modelFactory = modelFactory;
        this.store = InMemoryStore.getInstance().getStore(modelName);
        this.nextId = 1;
    }

    @Override
    public MappableModel<T> create(T data) throws UnknownModelException {
        MappableModel<T> model = this.modelFactory.build(
                modelName,
                data
        );

        model.setId(this.nextId++);
        this.store.put(model.getId(), model.getData());

        loadRelations(model);
        return model;
    }

    @Override
    public void delete(MappableModel<T> model) throws InvalidStorageReferenceException {
        if (this.store.containsKey(model.getId())) {
            this.store.remove(model.getId());
        } else {
            throw new InvalidStorageReferenceException(model.getName(), model.getId());
        }
    }

    @Override
    public Collection<MappableModel<T>> find(Query query) throws UnknownModelException {
      var result = new ArrayList<MappableModel<T>>();
      for (var entry: this.store.entrySet()) {
        var value = entry.getValue();
        var key = entry.getKey();
        var valueModel = this.modelFactory.build(this.modelName, value);
        valueModel.setId(key);
        try {
          if (isNodeSatisfied(valueModel.map(), query.getRoot())) {
            result.add(valueModel);
          }
        } catch (InvalidQueryOperation ignored) {

        }
      }
      return result;
    }

    private boolean isNodeSatisfied(FieldsMap props, QueryNode node) throws InvalidQueryOperation {
      if (node.getType() == QueryNodeType.CLAUSE) {
        return isOperationSatisfied(props, node);
      }

      var children = node.getChildren();
      if (children.isEmpty()) return true;

      boolean result;
      BiFunction<Boolean, Boolean, Boolean> op = (a, b) -> false;
      switch (node.getType()) {
        case AND_OPERATION -> {
          result = true;
          op = (a, b) -> a && b;
        }
        case OR_OPERATION -> {
          result = false;
          op = (a, b) -> a || b;
        }
        case XOR_OPERATION -> {
          result = false;
          op = (a, b) -> a ^ b;
        }
        default -> throw new InvalidQueryOperation(node.getType());
      }

      for (var child: children) {
        result = op.apply(result, isOperationSatisfied(props, child));
      }

      return result;
    }

    private boolean isOperationSatisfied(FieldsMap props, QueryNode node) throws InvalidQueryOperation {
      assert node.getType() == QueryNodeType.CLAUSE;
      var clause = node.getClause();
      var clauseField = clause.getField();
      var clauseOperation = clause.getOperation();

      var foundField = props.getMap().get(clauseField.getName());
      switch (foundField.getType()) {
        case String:
          var isSameString = foundField.getValue().equals(clauseField.getValue());
          switch (clauseOperation) {
            case Like:
              return isSameString;
            case NotLike:
              return !isSameString;
            default:
              break;
          }
          break;

        case Boolean:
          var boolValue = Boolean.parseBoolean(foundField.getValue());
          switch (clauseOperation) {
            case IsTrue:
              return boolValue;
            case IsFalse:
              return !boolValue;
            default:
              break;
          }
          break;

        case Integer:
          var foundInteger = Integer.parseInt(foundField.getValue());
          var queryInteger = Integer.parseInt(clauseField.getValue());
          switch (clauseOperation) {
            case Equal:
              return foundInteger == queryInteger;
            case NotEqual:
              return foundInteger != queryInteger;
            case LessThan:
              return foundInteger < queryInteger;
            case LessEqualThan:
              return foundInteger <= queryInteger;
            case GreaterThan:
              return foundInteger > queryInteger;
            case GreaterEqualThan:
              return foundInteger >= queryInteger;
            default:
              break;
          }
          break;

        case Reference:
          if (clauseOperation == QueryOperation.IsSame) {
            return foundField.getValue().equals(clauseField.getValue());
          }
          break;
      }

      throw new InvalidQueryOperation(foundField, clauseOperation);
    }

    @Override
    public Optional<MappableModel<T>> findOne(Query query) throws UnknownModelException {
      return this.find(query).stream().findFirst();
    }

    @Override
    public Optional<MappableModel<T>> findById(Integer id) throws UnknownModelException {
        if (this.store.containsKey(id)) {
            var data = this.store.get(id);
            var model = modelFactory.build(
                    modelName,
                    data
            );
            model.setId(id);
            return Optional.of(model);
        }
        return Optional.empty();
    }

    @Override
    public void update(MappableModel<T> model, FieldsMap values) throws InvalidStorageReferenceException {
        if (this.store.containsKey(model.getId())) {
            model.unmapIfSet(model.getData(), values);
            this.store.put(model.getId(), model.getData());
        } else {
            throw new InvalidStorageReferenceException(model.getName(), model.getId());
        }
    }

  @Override
  public void loadRelations(MappableModel<T> model) throws UnknownModelException {
    var relatedFields = model.getRelatedFields();
    var queryNodeFactory = new QueryNodeFactory();

    for (var relatedField: relatedFields) {
      var modelToLoad = relatedField.getTargetModel();
      var isSelfSource = true;
      if (this.modelName.equals(modelToLoad)) {
        modelToLoad = relatedField.getSourceModel();
        isSelfSource = false;
      }

      var relatedRepo = new InMemoryRepository<Object>(modelToLoad, this.modelFactory);
      switch (relatedField.getRelationType()) {
        case ONE_ONWS_MANY -> {
          if (isSelfSource) {
            // Source is loading the target
            // TODO: ...
          } else {
            // Target is loading the source
            var query = new Query(
              queryNodeFactory.buildClause(
                new Field(relatedField.getForeignKeyFieldOnSource(), FieldType.Reference),
                QueryOperation.IsSame
              )
            );
            var foundEntity = relatedRepo.findOne(query);
            foundEntity.ifPresent(objectMappableModel -> model.loadField(
              relatedField.getTargetModelRefField().getName(), objectMappableModel)
            );
          }
        }

        case ONE_OWNS_ANOTHER -> {
          if (isSelfSource) {

          } else {

          }
        }
      }
    }
  }
}
