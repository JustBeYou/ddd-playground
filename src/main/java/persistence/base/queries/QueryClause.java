package persistence.base.queries;

import lombok.Getter;
import lombok.NonNull;
import persistence.base.serialization.Field;

public class QueryClause {
    @NonNull
    @Getter
    private final Field field;
    @NonNull
    @Getter
    private final QueryOperation operation;

    QueryClause(Field field, QueryOperation queryOperation) {
        this.field = field;
        this.operation = queryOperation;
    }
}
