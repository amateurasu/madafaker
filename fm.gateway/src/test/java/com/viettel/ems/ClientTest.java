package com.viettel.ems;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

public class ClientTest {
    @Test
    public void a() {
        var client = WebClient.create("http://127.0.0.1:9000");
        var responseSpec = client.get().uri("/fm/v1.0/test").retrieve();
        System.out.println(responseSpec);
    }
}
