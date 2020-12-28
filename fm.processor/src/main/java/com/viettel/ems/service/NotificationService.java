package com.viettel.ems.service;

import com.viettel.ems.model.EventHandlePayload;
import com.viettel.ems.model.cache.EventCategoryCache;
import com.viettel.ems.model.cache.UserCache;
import com.viettel.ems.model.cm.Response;
import com.viettel.ems.model.entity.Event;
import com.viettel.ems.model.event.NewMessageAppEvent;
import com.viettel.ems.model.event.NotificationAppEvent;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Data
@Slf4j
@Service
public class NotificationService {

    @Value("${ems.wm.address}")
    private String wmAddress;
    private final UserCache userCache;
    private final EventCategoryCache eventCategoryCache;


    @EventListener
    @Async("computeExecutor")
    public void eventNotify(NewMessageAppEvent evt) {
        var payload = evt.getPayload();
        var event = evt.getEvent();

        log.info("payload {}", payload);
        sendNotification(payload);
    }

    private void sendNotification(EventHandlePayload payload) {
        var map = new LinkedMultiValueMap<String, String>();
        map.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        map.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        var request = new HttpEntity<>(payload, map);
        var entity = new RestTemplate().postForEntity(wmAddress, request, String.class);
        var body = entity.getBody();
        log.info("Command {} -> {}", payload, body);
    }
}
