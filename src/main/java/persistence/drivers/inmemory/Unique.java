package persistence.drivers.inmemory;

import lombok.EqualsAndHashCode;
import persistence.base.MappableModel;
import persistence.base.Repository;
import persistence.base.exceptions.InvalidQueryOperation;
import persistence.base.queries.Query;
import persistence.base.queries.QueryFactory;
import persistence.base.queries.QueryOperation;
import persistence.base.serialization.Field;

@EqualsAndHashCode
public class Unique<T> implements RuntimeConstraint<T> {
    private final Field field;

    public Unique(Field field) {
        this.field = field;
    }

    @Override
    public boolean isSatisfied(MappableModel<T> model, Repository<T> repository) throws InvalidQueryOperation {
        var queryNodeFactory = new QueryFactory();
        var query = new Query(
            queryNodeFactory.buildClause(
                new Field(
                    this.field.getName(),
                    this.field.getType(),
                    model.map().getMap().get(this.field.getName()).getValue()
                ),
                QueryOperation.IsSame
            )
        );
        var found = repository.find(query);

        return model.getId() == null && found.isEmpty() ||
            model.getId() != null && found.size() == 1;
    }

    @Override
    public String getFailingMessage() {
        return "Field value is not unique: " + this.field.getName();
    }
}
