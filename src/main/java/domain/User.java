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
    @NonNull private String name;
    @NonNull private String passwordHash;
    @NonNull private String email;
    @NonNull private Collection<Right> rights;
    @NonNull private Collection<ReadingTracker> trackers;
    @NonNull private Collection<Review> reviews;

    public User(@NonNull String name, @NonNull String password, @NonNull String email) {
        this(name, password, email, false);
    }

    public User(@NonNull String name, @NonNull String password, @NonNull String email, boolean copy) {
        if (copy) {
            this.passwordHash = password;
        } else {
            this.passwordHash = this.hash(password);
        }
        this.name = name;
        this.email = email;
        this.rights = new ArrayList<>();
        this.trackers = new ArrayList<>();
        this.reviews = new ArrayList<>();
    }

    public boolean isPasswordValid(String password) {
        return this.hash(password).equals(this.passwordHash);
    }

    public boolean hasRight(Right right) {
        return hasRight(right.getType());
    }

    public boolean hasRight(RightType right) {
        for (var existingRight : this.rights) {
            if (existingRight.getType().equals(right)) {
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
