package com.viettel.ems;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.*;

// @EnableKafka
@EnableConfigurationProperties
@SpringBootApplication
public class Gateway {
    public static void main(String[] args) {
        SpringApplication.run(Gateway.class, args);
    }

    @Bean("executorService")
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(50);
    }

    @Bean
    public <T> CompletionService<T> completionService(@Qualifier("executorService") Executor executor) {
        return new ExecutorCompletionService<>(executor);
    }
}
