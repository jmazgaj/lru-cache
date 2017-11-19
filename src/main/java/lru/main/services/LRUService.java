package lru.main.services;

import org.springframework.http.HttpHeaders;

public interface LRUService {

    Object get(Object key);

    Object put(Object key, Object value);

    Object put(Object key, Object value, String name);

    void setCapacity(int size);

    HttpHeaders getHttpHeaders(Object key);
}
