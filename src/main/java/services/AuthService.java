package services;

import auth.Identity;
import auth.InvalidLogin;
import domain.Right;
import domain.RightType;
import domain.User;
import lombok.Data;
import persistence.base.Repository;
import persistence.base.exceptions.CreationException;

@Data
public class AuthService {
  private final Repository<User> userRepository;
  private final Repository<Right> rightRepository;

  public Identity login(String user, String password) throws InvalidLogin {
    try {
      return new Identity(user, password, userRepository);
    } catch (Exception ex) {
      throw new InvalidLogin();
    }
  }

  public void register(String user, String password, String email) throws CreationException {
    var newUser = new User(user, password, email);
    userRepository.create(newUser);
    var defaultRight = new Right(RightType.BORROW_BOOKS, user);
    rightRepository.create(defaultRight);
  }
}
