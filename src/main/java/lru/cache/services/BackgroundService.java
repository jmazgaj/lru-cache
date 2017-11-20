package lru.cache.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BackgroundService {

    private LRUService lruService;

    @Autowired
    public BackgroundService(LRUService lruService) {
        this.lruService = lruService;
    }

    @Scheduled(fixedRate = 10000)
    public void removeUnusedKeys() {
        lruService.removeUnusedKeyNames();
    }
}


