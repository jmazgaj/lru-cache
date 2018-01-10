package lru.cache.services;

import lru.cache.cache.Cache;
import lru.cache.cache.LRUCache;
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

    private Cache lruCache;
    private Map<Object, String> keyNameMap;

    @Autowired
    public LRUServiceImpl(@Value("${lru.cache.size}") int initialCapacity) {
        logger.info("LRUService init. Initial capacity: {}", initialCapacity);
        this.lruCache = new LRUCache<>(initialCapacity, 0.75f, true);
        this.keyNameMap = new ConcurrentHashMap<>();
    }

    @Override
    public Object get(Object key) {
        logger.info("Retrieving value for key: {}", key);
        return lruCache.get(key);
    }

    @Override
    public Object put(Object key, Object value) {
        return put(key, value, "");
    }

    @Override
    public Object put(Object key, Object value, String name) {
        logger.info("Setting value for key: {}", key);
        keyNameMap.put(key, name);
        return lruCache.put(key, value);
    }

    @Override
    public void setCapacity(int capacity) {
        logger.info("Changing capacity. Current: {}, new: {}", capacity, capacity);
        this.lruCache.setCapacity(capacity);
    }

    @Override
    public int getCapacity() {
        return this.lruCache.getCapacity();
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
            if (!lruCache.containsKey(key)) {
                logger.info("Removing key {}.", key);
                keyNameMap.remove(key);
                ++counter;
            }
        }
        logger.info("Finished removing keys. Removed {} keys.", counter);
        return counter;
    }
}
