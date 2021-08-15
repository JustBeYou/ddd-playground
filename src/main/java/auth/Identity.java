package auth;

import domain.User;
import lombok.Getter;
import persistence.base.Repository;
import persistence.base.exceptions.InvalidQueryOperation;
import persistence.base.queries.Query;
import persistence.base.queries.QueryFactory;
import persistence.base.queries.QueryOperation;
import persistence.base.serialization.Field;
import persistence.base.serialization.FieldType;

public class Identity {
  @Getter
  private final User currentUser;

  public Identity(String user, String password, Repository<User> userRepository) throws InvalidLogin, InvalidQueryOperation {
    var queryBuilder = new QueryFactory();
    var query = new Query(
      queryBuilder.buildClause(
        new Field("name", FieldType.String, user),
        QueryOperation.Like
      )
    );

    var foundUser = userRepository.findOne(query);
    if (foundUser.isEmpty()) {
      throw new InvalidLogin();
    }

    var userObj = foundUser.get();
    if (!userObj.getData().isPasswordValid(password)) {
      throw new InvalidLogin();
    }

    userRepository.loadRelations(userObj);
    this.currentUser = userObj.getData();
  }
}