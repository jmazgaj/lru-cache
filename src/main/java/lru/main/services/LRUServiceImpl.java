package lru.main.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class LRUServiceImpl implements LRUService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Map<Object, Object> cache;
    private Map<Object, String> valueType;
    private int cacheCapacity;

    @Autowired
    public LRUServiceImpl(@Value("${lru.cache.size}") int initialCapacity) {
        logger.info("LRUService init. Initial capacity: {}", initialCapacity);
        this.cacheCapacity = initialCapacity;
        this.cache = new LinkedHashMap<Object, Object>(initialCapacity *4/3, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Object, Object> eldest) {
                return size() > cacheCapacity;
            }
        };
        this.valueType = new HashMap<>();
    }

    @Override
    public Object get(Object key) {
        logger.info("Retrieving value for key: {}, type: {}", key);
        return cache.get(key);
    }

    @Override
    public Object put(Object key, Object value) {
        logger.info("Setting value: {}, for key: {}", value, key);
        return cache.put(key, value);
    }

    @Override
    public void setCapacity(int capacity) {
        logger.info("Changing capacity. Current: {}, new: {}", cacheCapacity, capacity);
        this.cacheCapacity = capacity;
    }

    public final Map<Object, Object> getCache() {
        return this.cache;
    }
}
