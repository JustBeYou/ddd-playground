package services;

import auth.Identity;
import auth.InvalidLogin;
import domain.User;
import lombok.Getter;
import lombok.Setter;

public class SessionStatefulService {
    private Identity identity;
    private final AuthService authService;

    public SessionStatefulService(AuthService authService) {
        this.authService = authService;
    }

    public void login(String user, String password) throws InvalidLogin {
        this.identity = this.authService.login(user, password);
    }

    public void logout() {
        this.identity = null;
    }

    public User getCurrentUser() {
        if (isLoggedIn()) {
            return identity.getCurrentUser();
        }
        return null;
    }

    public boolean isLoggedIn() {
        return identity != null;
    }
}
