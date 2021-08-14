package services;

import auth.Identity;
import auth.InvalidLogin;
import domain.User;
import persistence.base.Repository;
import persistence.base.exceptions.CreationException;

public class AuthService {
  private final Repository<User> userRepository;

  public AuthService(Repository<User> userRepository) {
    this.userRepository = userRepository;
  }

  Identity login(String user, String password) throws InvalidLogin {
    return new Identity(user, password, userRepository);
  }

  void register(String user, String password, String email) throws CreationException {
    var newUser = new User(user, password, email);
    userRepository.create(newUser);
  }
}
