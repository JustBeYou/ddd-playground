package domain;

import lombok.Data;
import lombok.NonNull;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;

@Data
public class User {
    @NonNull String name;
    @NonNull String passwordHash;
    @NonNull String email;
    @NonNull Collection<Right> rights;

    public User(@NonNull String name, @NonNull String password, @NonNull String email) {
        this.name = name;
        this.passwordHash = this.hash(password);
        this.email = email;
        this.rights = new ArrayList<>();
    }

    public boolean isPasswordValid(String password) {
        return this.hash(password).equals(this.passwordHash);
    }

    public boolean hasRight(Right right) {
        for (var existingRight : this.rights) {
            if (existingRight.type.equals(right.type)) {
                return true;
            }
        }
        return false;
    }

    public String hash(String text) {
        try {
            var digest = MessageDigest.getInstance("SHA-256");
            var hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            return new String(hash, StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException exception) {
            return text;
        }
    }
}
