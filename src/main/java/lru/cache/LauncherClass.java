package lru.cache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LauncherClass {
    public static void main(String[] args) {
        SpringApplication.run(LauncherClass.class, args);
    }
}
