package auth;

public class InvalidLogin extends Exception {
  public InvalidLogin() {
    super("Incorrect user or password.");
  }
}
