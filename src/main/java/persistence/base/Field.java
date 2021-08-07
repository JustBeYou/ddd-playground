package persistence.base;

import lombok.Getter;
import lombok.NonNull;

public class Field {
    @NonNull @Getter private final String name;
    @Getter private final FieldType type;
    @Getter private final String value;

    public Field(String name, FieldType type) {
        this.name = name;
        this.type = type;
        this.value = null;
    }

    public Field(String name, FieldType type, String value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public Field(String name, String value) {
      this.name = name;
      this.type = null;
      this.value = value;
    }
}
