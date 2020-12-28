package com.viettel.ems;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
@EnableAsync
@EnableScheduling
@EnableConfigurationProperties
@SpringBootApplication
public class API {

    /** The entry point of application. */
    public static void main(String[] args) {
        SpringApplication.run(API.class, args);
    }

    @Bean("computeExecutor")
    public TaskExecutor computeExecutor() {
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("Compute Processing");
        executor.initialize();
        return executor;
    }
}
