package lru.cache.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LRUServiceImpl implements LRUService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Map<Object, Object> cache;
    private Map<Object, String> keyNameMap;
    private int capacity;

    @Autowired
    public LRUServiceImpl(@Value("${lru.cache.size}") int initialCapacity) {
        logger.info("LRUService init. Initial capacity: {}", initialCapacity);
        this.capacity = initialCapacity;
        this.cache = new LinkedHashMap<Object, Object>(initialCapacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Object, Object> eldest) {
                return size() > capacity;
            }
        };
        this.keyNameMap = new ConcurrentHashMap<>();
    }

    @Override
    public Object get(Object key) {
        logger.info("Retrieving value for key: {}", key);
        return cache.get(key);
    }

    @Override
    public Object put(Object key, Object value) {
        return put(key, value, "");
    }

    @Override
    public Object put(Object key, Object value, String name) {
        logger.info("Setting value for key: {}", key);
        keyNameMap.put(key, name);
        return cache.put(key, value);
    }

    @Override
    public void setCapacity(int capacity) {
        logger.info("Changing capacity. Current: {}, new: {}", this.capacity, capacity);
        this.capacity = capacity;
    }

    @Override
    public int getCapacity() {
        return this.capacity;
    }

    @Override
    public HttpHeaders getHttpHeaders(Object key) {
        HttpHeaders headers = new HttpHeaders();
        String fileName = keyNameMap.get(key);
        if (fileName != null && !"".equals(fileName)) {
            headers.setContentDispositionFormData(fileName, fileName);
            headers.setContentType(MediaType.parseMediaType("application/octet-stream"));
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        } else {
            headers.setContentType(MediaType.TEXT_PLAIN);
        }
        return headers;
    }

    @Override
    public int removeUnusedKeyNames() {
        logger.info("Started removing keys from keyNameMap.");
        int counter = 0;
        for (Object key : keyNameMap.keySet()) {
            if (!cache.containsKey(key)) {
                logger.info("Removing key {}.", key);
                keyNameMap.remove(key);
                ++counter;
            }
        }
        logger.info("Finished removing keys. Removed {} keys.", counter);
        return counter;
    }
}
