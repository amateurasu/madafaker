package com.viettel.ems;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

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

    @Bean("jdbcExecutor")
    public TaskExecutor jdbcExecutor() {
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(50);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("JDBC Processing");
        executor.initialize();
        return executor;
    }
}
