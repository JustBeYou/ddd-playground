package models;

import java.util.Map;

public interface Mappable<T> {
    ModelMap map();
    T unmap(ModelMap map);
}
