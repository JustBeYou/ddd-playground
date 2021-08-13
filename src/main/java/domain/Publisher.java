package domain;

import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class Publisher {
    @NonNull
    private final String name;
    private final Country country;
}
