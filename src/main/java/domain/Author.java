package domain;

import lombok.Data;
import lombok.NonNull;

@Data
public class Author {
    @NonNull
    private final String name;
    private final Country country;
}
