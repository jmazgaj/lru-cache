package lru.cache.cache;

public interface Cache<K, V> {
    Object get(K key);

    Object put(K key, V value);

    void setCapacity(int capacity);

    int getCapacity();

    boolean containsKey(K key);
}
