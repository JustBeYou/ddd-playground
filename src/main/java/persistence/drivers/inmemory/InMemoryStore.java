package persistence.drivers.inmemory;

import java.util.HashMap;
import java.util.Map;

public class InMemoryStore {
    static private InMemoryStore instance;
    private final Map<String, Map<Integer, Object>> database;

    private InMemoryStore() {
        database = new HashMap<>();
    }

    static public InMemoryStore getInstance() {
        if (instance == null) {
            instance = new InMemoryStore();
        }
        return instance;
    }

    public <T> Map<Integer, T> getStore(String name) {
        if (!database.containsKey(name)) {
            database.put(name, new HashMap<>());
        }
        return (Map<Integer, T>) database.get(name);
    }

    public static void destroy() {
        instance = null;
    }
}
