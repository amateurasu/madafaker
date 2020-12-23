package com.viettel.ems.service;

import com.viettel.ems.model.cache.EventCategoryCache;
import com.viettel.ems.model.cache.UserCache;
import com.viettel.ems.model.event.NotificationAppEvent;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Data
@Slf4j
@Service
public class NotificationService {

    private final UserCache userCache;
    private final EventCategoryCache eventCategoryCache;

    // private final UserNotificationCache userNotificationCache;

    @Value("${ems.wm.address}")
    private String wmAddress;

    @EventListener
    @Async("computeExecutor")
    public void eventNotify(NotificationAppEvent event) {
        // TODO implement this
        log.info("Got event: {}", event.getNotification());
    }

    private void sendNotification(Map<String, ?> notification) {
        //     var headers = new HttpHeaders();
        //     headers.setContentType(MediaType.APPLICATION_JSON);
        //     headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        //
        //     var entity = new HttpEntity<>(notification, headers);
        //     var response = new RestTemplate().postForEntity(wmAddress, entity, String.class);
        var responseMono = WebClient.create()
            .post()
            .uri(wmAddress)
            .contentType(APPLICATION_JSON)
            .bodyValue(notification)
            .retrieve().bodyToMono(String.class);
        log.info("Response {}", responseMono);
    }
}
