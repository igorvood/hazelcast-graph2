package ru.vood.hazelcastgraph2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HazelcastGraph2Application {

    public static void main(String[] args) {
        SpringApplication.run(HazelcastGraph2Application.class, args);
    }

}
