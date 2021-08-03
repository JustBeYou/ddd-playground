package persistence.base;

import lombok.Data;
import lombok.NonNull;

import java.util.Map;

@Data
public class FieldsMap {
    @NonNull Map<String, Field> map;
    @NonNull String name;
}
