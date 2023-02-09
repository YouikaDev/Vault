package dev.youika.mysql;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SQLSection {

    private final Map<String, Object> valuesMap = new ConcurrentHashMap<>();

    public void cleanup() {
        valuesMap.clear();
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(final String key) {
        return (T) valuesMap.get(key);
    }

    void putValue(final String key, final Object val) {
        valuesMap.put(key, val);
    }

}
