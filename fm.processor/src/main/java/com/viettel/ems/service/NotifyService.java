package com.viettel.ems.service;

import com.viettel.ems.model.entity.Event;
import com.viettel.ems.model.event.NewMessageAppEvent;
import com.viettel.ems.model.event.NotificationAppEvent;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Data
@Component
public class NotifyService {

    @Value("${ems.wm.address}")
    private String wmAddress;

    @Async
    @EventListener
    public void notifyFault(NewMessageAppEvent appEvent) {
        var fault = appEvent.getEvent();
        notify(fault);
    }

    @Async
    @EventListener
    public void notifyEvent(NotificationAppEvent appEvent) {
        var notification = appEvent.getNotification();
        notify(notification);
    }

    private void notify(Event event) {
        var webClient = WebClient.builder().baseUrl(wmAddress).build();
        var req = checkRules(event);

        var response = webClient.post().bodyValue(req).retrieve();
    }

    private Map<String, ?> checkRules(Event event) {
        //@formatter:off
        var metadata = Map.of(
            "subject", "This is Subject of Mail",
            "contentEmail", "This is Content of Mail",
            "contentSMS", "Nội dung tin nhắn");
        return Map.of(
            "listEmail", List.of(),
            "listPhone", List.of(),
            "metadata", metadata
        );
        //@formatter:on
    }
}
