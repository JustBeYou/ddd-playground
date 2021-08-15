package persistence.drivers.inmemory;

import persistence.base.MappableModel;
import persistence.base.Repository;
import persistence.base.RepositoryFactory;
import persistence.base.constraints.Constraint;
import persistence.base.exceptions.CreationException;
import persistence.base.exceptions.InvalidQueryOperation;
import persistence.base.exceptions.InvalidStorageReferenceException;
import persistence.base.exceptions.UpdateException;
import persistence.base.models.ModelFactory;
import persistence.base.queries.*;
import persistence.base.serialization.Field;
import persistence.base.serialization.FieldType;
import persistence.base.serialization.FieldsMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

public class InMemoryRepository<T> implements Repository<T> {
    private final ModelFactory<T> modelFactory;
    private final Map<Integer, T> store;
    private final String modelName;
    private final Collection<RuntimeConstraint<T>> constraints;
    private final RepositoryFactory repositoryFactory;
    private Integer nextId;

    public InMemoryRepository(ModelFactory<T> modelFactory, RepositoryFactory repositoryFactory) {
        this.modelFactory = modelFactory;
        this.modelName = modelFactory.getModelName();
        this.store = InMemoryStore.getInstance().getStore(this.modelName);
        this.nextId = 1;
        this.repositoryFactory = repositoryFactory;
        this.constraints = new ArrayList<>();
    }

    @Override
    public MappableModel<T> create(T data) throws CreationException {
        MappableModel<T> model = this.modelFactory.build(data);
        for (var constraint : this.constraints) {
            boolean good = true;
            String message = null;
            try {
                if (!constraint.isSatisfied(model, this)) {
                    good = false;
                    message = constraint.getFailingMessage();
                }
            } catch (Exception ignored) {
                good = false;
                message = "Constraint checking thrown exception.";
            }

            if (!good) {
                throw new CreationException(message);
            }
        }

        model.setId(this.nextId++);
        this.store.put(model.getId(), model.getData());

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
    public Collection<MappableModel<T>> find(Query query) {
        var result = new ArrayList<MappableModel<T>>();
        for (var entry : this.store.entrySet()) {
            var value = entry.getValue();
            var key = entry.getKey();
            var valueModel = this.modelFactory.build(value);
            valueModel.setId(key);

            try {
                if (isNodeSatisfied(valueModel.map(), query.getRoot())) {
                    result.add(valueModel);
                }
            } catch (InvalidQueryOperation e) {
                System.out.println("[WARNING] Invalid query operation: " + e.getMessage());
            }
        }
        return result;
    }

    private boolean isNodeSatisfied(FieldsMap props, QueryNode node) throws InvalidQueryOperation {
        if (node == null) {
            return true;
        }

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

        for (var child : children) {
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
        if (foundField.getType() != clauseField.getType() &&
            clauseField.getType() != FieldType.Auto) {
            return false;
        }

        switch (foundField.getType()) {
            case String:
                if (clauseOperation == QueryOperation.IsSame) {
                    return foundField.getValue().equals(clauseField.getValue());
                } else if (clauseOperation == QueryOperation.Like) {
                    var pattern = Pattern.compile(clauseField.getValue());
                    return pattern.matcher(foundField.getValue()).find();
                } else if (clauseOperation == QueryOperation.NotLike) {
                    var pattern = Pattern.compile(clauseField.getValue());
                    return !pattern.matcher(foundField.getValue()).find();
                }
                break;

            case Boolean:
                var boolValue = Boolean.parseBoolean(foundField.getValue());
                switch (clauseOperation) {
                    case IsSame:
                        return boolValue == Boolean.parseBoolean(clauseField.getValue());
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
                    case IsSame:
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
                break;
        }

        throw new InvalidQueryOperation(foundField, clauseOperation);
    }

    @Override
    public Optional<MappableModel<T>> findOne(Query query) {
        return this.find(query).stream().findFirst();
    }

    @Override
    public Optional<MappableModel<T>> findById(Integer id) {
        if (this.store.containsKey(id)) {
            var data = this.store.get(id);
            var model = modelFactory.build(data);
            model.setId(id);
            return Optional.of(model);
        }
        return Optional.empty();
    }

    @Override
    public boolean exists(Query query) {
        var found = this.find(query);
        return found.size() > 0;
    }

    @Override
    public void update(MappableModel<T> model, FieldsMap values) throws InvalidStorageReferenceException, UpdateException {
        if (this.store.containsKey(model.getId())) {
            model.unmapIfSet(model.getData(), values);

            for (var constraint : this.constraints) {
                boolean good = true;
                String message = null;
                try {
                    if (!constraint.isSatisfied(model, this)) {
                        good = false;
                        message = constraint.getFailingMessage();
                    }
                } catch (Exception ignored) {
                    good = false;
                    message = "Constraint checking thrown exception.";
                }

                if (!good) {
                    throw new UpdateException(message);
                }
            }

            this.store.put(model.getId(), model.getData());
        } else {
            throw new InvalidStorageReferenceException(model.getName(), model.getId());
        }
    }

    @Override
    public void loadRelations(MappableModel<T> model) throws InvalidQueryOperation {
        var relatedFields = model.getRelatedFields();

        for (var relatedField : relatedFields) {
            var modelToLoad = relatedField.getTargetModel();
            var isSelfSource = true;
            if (this.modelName.equals(modelToLoad)) {
                modelToLoad = relatedField.getSourceModel();
                isSelfSource = false;
            }

            var relatedRepo = this.repositoryFactory.build(modelToLoad);
            switch (relatedField.getRelationType()) {
                case ONE_OWNS_MANY -> {
                    if (isSelfSource) {
                        // Source is loading the target
                        var refField = relatedField.getForeignKeyFieldOnTarget();
                        var refValue = model
                            .map().getMap()
                            .get(relatedField.getForeignKeyFieldOnSource())
                            .getValue();
                        var query = buildSameQuery(refField, refValue);

                        var foundEntities = relatedRepo.find(query);
                        model.loadRelationField(relatedField.getSourceModelRefField().getName(), foundEntities);
                    } else {
                        // Target is loading the source
                        var refField = relatedField.getForeignKeyFieldOnSource();
                        var refValue = model
                            .map().getMap()
                            .get(relatedField.getForeignKeyFieldOnTarget())
                            .getValue();
                        var query = buildSameQuery(refField, refValue);

                        var foundEntity = relatedRepo.findOne(query);
                        foundEntity.ifPresent(objectMappableModel -> model.loadRelationField(
                            relatedField.getTargetModelRefField().getName(), objectMappableModel.getData())
                        );
                    }
                }

                case ONE_OWNS_ANOTHER -> {
                    if (isSelfSource) {
                        // TODO: implement this
                    } else {
                        // TODO: implement this
                    }
                }
            }
        }
    }

    @Override
    public void addConstraint(Constraint constraint, Field field) {
        if (constraint == Constraint.UNIQUE) {
            var newConstraint = new Unique<T>(field);
            boolean already = this.constraints.contains(newConstraint);
            if (!already) {
                this.constraints.add(newConstraint);
            }
        }
    }

    private Query buildSameQuery(String refField, String refValue) {
        var queryNodeFactory = new QueryFactory();
        return new Query(
            queryNodeFactory.buildClause(
                new Field(
                    refField,
                    FieldType.Auto,
                    refValue
                ),
                QueryOperation.IsSame
            )
        );
    }
}
