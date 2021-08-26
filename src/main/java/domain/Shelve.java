package domain;

import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collection;

@Data
public class Shelve {
    @NonNull private String name;
    @NonNull private Collection<Book> books;

    public Shelve(@NonNull String name) {
        this.name = name;
        this.books = new ArrayList<>();
    }
}
