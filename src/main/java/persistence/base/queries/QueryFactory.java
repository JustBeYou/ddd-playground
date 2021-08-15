package persistence.base.queries;

import persistence.base.serialization.Field;

public class QueryFactory {
    public Query buildSimpleQuery(String name, String value, QueryOperation operation) {
        return new Query(this.buildClause(name, value, operation));
    }

    public QueryNode buildClause(String name, String value, QueryOperation operation) {
        return new QueryNode(
          new QueryClause(
              new Field(name, value),
              operation
          )
        );
    }

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
