package lru.cache.services;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class LRUServiceImplTest {

    private final int initialCapacity = 5;

    private LRUService lruService;

    @Before
    public void setup() {
       lruService = new LRUServiceImpl(initialCapacity);
    }

    @Test
    public void putGetTest() {
        Object nullValue = lruService.put("key1", "val1");
        Object value1 = lruService.get("key1");

        Assertions.assertThat(nullValue).isNull();
        Assertions.assertThat(value1).isEqualTo("val1");
    }

    @Test
    public void changeCapacityTest() {
        lruService.setCapacity(6);

        Assertions.assertThat(lruService.getCapacity()).isEqualTo(6);
    }

    @Test
    public void cacheOverflowTest() {
        lruService.put("myKey", "myValue");
        Object val = lruService.get("myKey");
        addElementsToCache(lruService, initialCapacity);

        Assertions.assertThat(val).isEqualTo("myValue");
        Assertions.assertThat(lruService.get("myKey")).isNull();
    }

    @Test
    public void cacheLRUGetTest() {
        addElementsToCache(lruService, initialCapacity);
        Object key1 = lruService.get("key1");
        addElementsToCache(lruService, 2);

        Assertions.assertThat(lruService.getCapacity()).isEqualTo(initialCapacity);
        Assertions.assertThat(lruService.get("key1")).isEqualTo(key1);
    }

    @Test
    public void removeUnusedKeysTest() {
        addElementsToCache(lruService, initialCapacity + 2);

        Assertions.assertThat(lruService.removeUnusedKeyNames()).isEqualTo(2);
    }

    private void addElementsToCache(LRUService lruService, int elementAmount) {
        for (int i = 0; i < elementAmount ; i++) {
            lruService.put("key" + i, "val" + i);
        }
    }

}