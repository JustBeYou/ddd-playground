package persistence.base;

public class QueryNodeFactory {
    QueryNode buildClause(Field field, QueryOperation operation) {
        return new QueryNode(new QueryClause(field, operation));
    }

    QueryNode buildAnd(QueryNode[] children) {
        return new QueryNode(QueryNodeType.AND_OPERATION, children);
    }

    QueryNode buildOr(QueryNode[] children) {
        return new QueryNode(QueryNodeType.OR_OPERATION, children);
    }

    QueryNode buildXor(QueryNode[] children) {
        return new QueryNode(QueryNodeType.XOR_OPERATION, children);
    }
}
