package models;

import lombok.Data;
import lombok.NonNull;

import java.util.Map;

@Data
public class ModelMap {
    @NonNull Map<String, Field> map;
    @NonNull String name;
}
