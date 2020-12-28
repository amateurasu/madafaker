package com.viettel.ems.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.ems.model.cm.Response;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ScriptConfigTest {
    @Test
    public void testJsonFormat() throws JsonProcessingException {
        var config = ScriptConfig.builder()
            .commandList(List.of("xyz"))
            .neIpList(List.of("abc", "def"))
            .stopOnError(true)
            // .cronExpression("0 0 6 * * *")
            .build();
        var mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(config));
    }

    @Test
    public void testCmConnection() throws JsonProcessingException {
        var client = WebClient.builder()
            .baseUrl("http://172.16.28.46:8088")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic ZHVjbG0yMjpwYXNzd29yZA==")
            .build();

        var config = new ScriptConfig(0, List.of("SHW CELL: ;"), List.of("172.16.31.211"), "5GA", false);
        var mapper = new ObjectMapper();
        var response = client.post()
            .uri("/configuration/5ga")
            .body(BodyInserters.fromValue(mapper.writeValueAsString(config)))
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .retrieve();

        System.out.println(response);
    }

    @Test
    public void a() throws JsonProcessingException {
        var restTemplate = new RestTemplate();
        var config = new ScriptConfig(0, List.of("SHW CELL: ;"), List.of("172.16.31.211"), "5GA", false);
        var mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(config));

        var map = new LinkedMultiValueMap<String, String>();
        map.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        map.add(HttpHeaders.AUTHORIZATION, "Basic ZHVjbG0yMjpwYXNzd29yZA==");

        var request = new HttpEntity<>(config, map);
        var foo = restTemplate.postForEntity("http://172.16.28.46:8088/configuration/5ga", request, Response.class);
        assertEquals(foo.getStatusCode(), HttpStatus.OK);
        var body = foo.getBody();
        assertNotNull(body);
        assertEquals(body.getResponse(), "SUCCESS");
        System.out.println(foo);
    }
}
