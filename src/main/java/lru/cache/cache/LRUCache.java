package lru.cache.cache;

import java.util.LinkedHashMap;
import java.util.Map;


public class LRUCache<K, V> extends LinkedHashMap<K, V> implements Cache<K, V>{

    private int capacity;

    public LRUCache(int initialCapacity,
                    float loadFactor,
                    boolean accessOrder) {
        super(initialCapacity, loadFactor, accessOrder);
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public Object put(Object key, Object value) {
        return put(key, value);
    }
}
