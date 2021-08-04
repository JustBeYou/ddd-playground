package persistence.base;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;

@Data
public class QueryNode {
    private final ArrayList<QueryNode> children;
    private final QueryClause clause;
    private final QueryNodeType type;

    public QueryNode(QueryNodeType type, QueryNode[] children) {
        assert type != QueryNodeType.CLAUSE;
        this.clause = null;
        this.type = type;
        this.children = new ArrayList<>();
        this.children.addAll(Arrays.asList(children));
    }

    public QueryNode(QueryClause clause) {
        this.type = QueryNodeType.CLAUSE;
        this.children = null;
        this.clause = clause;
    }
}
