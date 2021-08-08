package persistence.base.serialization;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

@Data
public class Field {
    @NonNull private final String name;
    private final FieldType type;
    private final String value;

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
      this.type = FieldType.Auto;
      this.value = value;
    }
}
