package lru.main.services;

import java.util.StringJoiner;

import static org.junit.Assert.*;

public class LRUServiceImplTest {

    public static void main(String[] args) {
        LRUServiceImpl service = new LRUServiceImpl(5);
        putAndShow(service,"key1", "val1");
        putAndShow(service,"key2", "val2");
        putAndShow(service,"key3", "val3");
        putAndShow(service,"key4", "val4");
        putAndShow(service,"key5", "val5");
        System.out.println("GET: " + service.get("key1"));
        putAndShow(service,"key6", "val6");
        service.setCapacity(6);
        putAndShow(service,"key7", "val7");
    }

    private static String putAndShow(LRUServiceImpl service, String key, Object object) {
        service.put(key, object);
        StringJoiner stringJoiner = new StringJoiner("\n");
        System.out.println("Showing map elements: ");
        final int[] iterator = new int[1];
        iterator[0] = 1;
        service.getCache().forEach((k,v) -> {
            stringJoiner.add(iterator[0] + ". " + k + " -> " + v);
            System.out.println(iterator[0] + ". " + k + " -> " + v);
            iterator[0]++;
        });
        return stringJoiner.toString();
    }

}