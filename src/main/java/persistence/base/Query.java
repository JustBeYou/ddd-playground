package persistence.base;

import lombok.Getter;
import lombok.NonNull;

public class Query {
    @NonNull @Getter private final String modelName;
    @NonNull @Getter private final QueryNode root;

    Query(String modelName, QueryNode root) {
        this.modelName = modelName;
        this.root = root;
    }
}
