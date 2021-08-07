package persistence.base.queries;

import lombok.Getter;
import lombok.NonNull;

public class Query {
    @NonNull @Getter private final QueryNode root;

    public Query(
      QueryNode root) {
        this.root = root;
    }
}
