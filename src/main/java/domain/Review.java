package domain;

import domain.exceptions.InvalidRating;
import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collection;

@Data
public class Review {
    @NonNull
    private Integer rating;

    @NonNull
    private String text;

    @NonNull
    private String bookName;
    private Book book;

    @NonNull
    private String userName;
    private User user;

    private Collection<Comment> comments;

    public Review(@NonNull Integer rating, @NonNull String text, @NonNull String bookName, @NonNull String userName) throws InvalidRating {
        if (rating < 1 || rating > 5) {
            throw new InvalidRating();
        }

        this.rating = rating;
        this.text = text;
        this.bookName = bookName;
        this.userName = userName;
        this.comments = new ArrayList<>();
    }
}
