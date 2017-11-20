package lru.cache.services;

import org.springframework.http.HttpHeaders;

public interface LRUService {

    Object get(Object key);

    Object put(Object key, Object value);

    Object put(Object key, Object value, String name);

    void setCapacity(int size);

    int getCapacity();

    HttpHeaders getHttpHeaders(Object key);

    int removeUnusedKeyNames();
}
