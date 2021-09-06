package domain;

import lombok.Data;
import lombok.NonNull;

@Data
public class Comment {
    @NonNull
    private String text;

    @NonNull
    private Integer reviewId;
    private Review review;

    @NonNull
    private String commenterName;
    private User commenter;

    public Comment(@NonNull String text, @NonNull Integer reviewId, @NonNull String commenterName) {
        this.text = text;
        this.reviewId = reviewId;
        this.commenterName = commenterName;
    }
}
