package com.viettel.ems.scheduler;

import com.viettel.ems.model.cm.Response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Data
@AllArgsConstructor
public class CommandRunner implements Runnable {

    private final String cmAddress;
    private final ScriptConfig config;

    @Override
    public void run() {
        var map = new LinkedMultiValueMap<String, String>();
        map.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        map.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        map.add(HttpHeaders.AUTHORIZATION, "Basic ZHVjbG0yMjpwYXNzd29yZA==");
        var request = new HttpEntity<>(config, map);
        var entity = new RestTemplate().postForEntity(cmAddress, request, Response.class);
        var body = entity.getBody();
        log.info("Command {} -> {}", config, body);
    }
}
