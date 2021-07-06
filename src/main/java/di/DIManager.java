package di;

import java.util.HashMap;
import java.util.Map;

public class DIManager {
    private static DIManager self = null;
    private final Map<String, Object> instances;

    private DIManager() {
        instances = new HashMap<>();
    }

    static public DIManager getInstance() {
        if (self == null) {
            self = new DIManager();
        }
        return self;
    }

    public void register(String name, Object obj) {
        instances.put(name, obj);
    }

    public <T> T get(String name) {
        return (T)(instances.get(name));
    }
}
