package persistence.base.queries;

import persistence.base.serialization.Field;

public class QueryNodeFactory {
    public QueryNode buildClause(Field field, QueryOperation operation) {
        return new QueryNode(new QueryClause(field, operation));
    }

    public QueryNode buildAnd(QueryNode[] children) {
        return new QueryNode(QueryNodeType.AND_OPERATION, children);
    }

    public QueryNode buildOr(QueryNode[] children) {
        return new QueryNode(QueryNodeType.OR_OPERATION, children);
    }

    public QueryNode buildXor(QueryNode[] children) {
        return new QueryNode(QueryNodeType.XOR_OPERATION, children);
    }
}
