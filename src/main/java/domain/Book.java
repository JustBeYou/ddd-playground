package domain;

import lombok.Data;
import lombok.NonNull;

@Data
public class Book {
    @NonNull
    private String name;
    @NonNull
    private String ISBN;
    @NonNull
    private String publishedAt;
}
