package models;

import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collection;

public class Query {
    @NonNull @Getter private final String modelName;
    @NonNull @Getter private final QueryType type;
    private final Collection<QueryClause> clauses = new ArrayList<>();

    Query(String modelName) {
        this.modelName = modelName;
        this.type = QueryType.Filter;
    }

    Query(String modelName, QueryType type) {
        this.modelName = modelName;
        this.type = type;
    }

    void addClause(QueryClause queryClause) {
        clauses.add(queryClause);
    }
    Iterable<QueryClause> getClauses() {
        return clauses;
    }
}
