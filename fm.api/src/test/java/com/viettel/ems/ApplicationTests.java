package com.viettel.ems;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = API.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTests {
    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private String getRootUrl() {
        // return "http://10.60.88.22:8080/fm";
        return "http://localhost:" + port + "/api/user";
    }

    @Test
    public void testCreateEventReport() {
        // create an instance of RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        // create headers
        HttpHeaders headers = new HttpHeaders();
        // set `content-type` header
        headers.setContentType(MediaType.APPLICATION_JSON);
        // set `accept` header
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // request body parameters
        Map<String, Object> event = new HashMap<>();
        List<Map<String, Object>> listevent = new ArrayList<Map<String, Object>>();
        //		List<Map> listevent = new ArrayList();
        event.put("event_type", "1");
        event.put("ne_type", "AMF");
        event.put("ne", "AMF1");
        event.put("subsystem", "abcd");
        event.put("alarm_id", "2048");
        event.put("keygen", "amf");
        event.put("level", "1");
        event.put("location", "1-1-1-1");
        event.put("trigger_time", Long.toString(System.currentTimeMillis()));
        event.put("nodename", "amf1@192.168.8.1");
        listevent.add(event);
        for (int i = 0; i < 1005; i++) {
            event.replace("keygen", "amf" + i);
            HttpEntity<List<Map<String, Object>>> entity = new HttpEntity<>(listevent, headers);
            restTemplate.postForEntity(getRootUrl() + "/alarmreport", entity, String.class);
        }
        //		System.out.println(listevent);

        // build the request
        //		HttpEntity<List<Map<String, Object>>> entity = new HttpEntity<>(listevent, headers);

        // send POST request
        //		ResponseEntity<String> postResponse = restTemplate.postForEntity(getRootUrl() + "/eventreport",
        //		entity, String.class);

        //		// build the request
        //		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(event, headers);
        //
        //		// send POST request
        //		ResponseEntity<String> postResponse = restTemplate.postForEntity(getRootUrl() + "/alarm/eventreport",
        //		entity, String.class);

        //		System.out.println(event.toString());
        //	System.out.println("postResponse " + postResponse.toString());

        //		Assert.assertNotNull(postResponse);
        //		Assert.assertNotNull(postResponse.getBody());
    }

    @Test
    public void testDeleteAlarmCurrent() {
        // create an instance of RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        // create headers
        HttpHeaders headers = new HttpHeaders();
        // set `content-type` header
        headers.setContentType(MediaType.APPLICATION_JSON);
        // set `accept` header
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // request body parameters
        Map<String, Object> event = new HashMap<>();
        event.put("event_type", "0");
        event.put("ne_type", "5GC");
        event.put("ne", "5GC1");
        event.put("subsystem", "amf");
        event.put("alarm_id", "2048");
        event.put("keygen", "amf1");
        event.put("level", "1");
        event.put("location", "1-1-1-1");
        event.put("trigger_time", Long.toString(System.currentTimeMillis()));
        event.put("nodename", "amf1@192.168.8.1");

        List<Map<String, Object>> listevent = new ArrayList<>();
        listevent.add(event);

        // build the request
        HttpEntity<List<Map<String, Object>>> entity = new HttpEntity<>(listevent, headers);
        // send POST request
        ResponseEntity<String> postResponse =
            restTemplate.postForEntity(getRootUrl() + "/eventreport", entity, String.class);
        assertNotNull(postResponse);
        //		Assert.assertNotNull(postResponse.getBody());
    }

    @Test
    public void testGetAllAlarmCurrent() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);

        ResponseEntity<String> response =
            restTemplate.exchange(getRootUrl() + "/alarmcurrent", HttpMethod.GET, entity, String.class);

        assertNotNull(response.getBody());
    }

    @Test
    public void testGetAlarmCurrent() {
        // create headers
        HttpHeaders headers = new HttpHeaders();
        // set `content-type` header
        headers.setContentType(MediaType.APPLICATION_JSON);
        // set `accept` header
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        Map<String, Object> event = new HashMap<>();
        List<Map<String, Object>> listevent = new ArrayList<Map<String, Object>>();

        event.put("event_type", "0");
        event.put("ne_type", "5GC");
        event.put("ne", "5GC1");
        event.put("subsystem", "amf");
        event.put("alarm_id", "2048");
        event.put("keygen", "amf1");
        event.put("level", "1");
        event.put("location", "1-1-1-1");
        event.put("trigger_time", Long.toString(System.currentTimeMillis()));
        event.put("nodename", "amf1@192.168.8.1");
        listevent.add(event);

        HttpEntity<List<Map<String, Object>>> entity = new HttpEntity<>(listevent, headers);

        System.out.println(entity);

        ResponseEntity<String> response =
            restTemplate.exchange(getRootUrl() + "/prov/alarm", HttpMethod.GET, entity, String.class);

        assertNotNull(response.getBody());
    }
}
