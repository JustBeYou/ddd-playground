package persistence.base;

import lombok.Getter;
import lombok.NonNull;

public class QueryClause {
    @NonNull @Getter private final Field field;
    @NonNull @Getter private final QueryOperation operation;

    QueryClause(Field field) {
        this.field = field;
        this.operation = QueryOperation.Update;
    }

    QueryClause(Field field, QueryOperation queryOperation) {
        this.field = field;
        this.operation = queryOperation;
    }
}
