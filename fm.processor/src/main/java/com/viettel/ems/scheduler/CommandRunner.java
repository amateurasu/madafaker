package com.viettel.ems.scheduler;

import com.viettel.ems.model.cm.Response;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Date;

@Data
@AllArgsConstructor
public class CommandRunner implements Runnable {

    @Value("ems.cm.address")
    private String cmAddress;

    @Value("ems.cm.uri")
    private String cmUri;

    private final String message;
    private ScheduleConfig config;

    @Override
    public void run() {
        var client = WebClient.builder()
            .baseUrl(cmAddress)
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic ZHVjbG0yMjpwYXNzd29yZA==")
            .build();

        var response = client.post().uri(cmUri).body(config, Response.class).retrieve();
    }
}
