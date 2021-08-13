package persistence.base.exceptions;

import persistence.base.queries.QueryNodeType;
import persistence.base.queries.QueryOperation;
import persistence.base.serialization.Field;

public class InvalidQueryOperation extends Exception {
    public InvalidQueryOperation(Field field, QueryOperation operation) {
        super("Mismatched field - type or unsupported operation. Field: " + field.toString() +
            " Operation: " + operation.toString());
    }

    public InvalidQueryOperation(QueryNodeType type) {
        super("Misused query clause: " + type.toString());
    }
}
