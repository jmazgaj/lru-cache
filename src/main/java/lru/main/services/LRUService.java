package lru.main.services;

public interface LRUService {

    Object get(Object key);
    Object put(Object key, Object value);
    void setCapacity(int size);
}
