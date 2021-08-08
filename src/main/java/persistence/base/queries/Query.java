package persistence.base.queries;

import lombok.Getter;

public class Query {
  @Getter
  private final QueryNode root;

  public Query(QueryNode root) {
    this.root = root;
  }

  public Query() {
    this.root = null;
  }
}
