package com.viettel.ems.api;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import com.viettel.ems.model.cache.NeCache;
import com.viettel.ems.model.cache.SiteCache;
import com.viettel.ems.model.entity.NE;
import com.viettel.ems.model.entity.NeSite;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onap.ves.DCAE;
import org.onap.ves.EventKey;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.viettel.ems.utils.ServerUtils.getClientIp;

@Slf4j
@RestController
@AllArgsConstructor
public class EventController_v1_0 {

    private final JsonFormat.Parser parser = JsonFormat.parser();

    private final KafkaTemplate<EventKey.Key, DCAE.Event> kafkaTemplate;
    private final NeCache neCache;
    private final SiteCache siteCache;

    @PostMapping({"/v1.0/event", "/eventListener/v7"})
    public ResponseEntity<Void> collectFault(@RequestBody String vesEventJson, ServerHttpRequest request) {
        String topic = null;
        try {
            var ip = getClientIp(request);
            var ne = neCache.get(ip);
            var site = siteCache.get(ne.getSiteId());
            var builder = DCAE.VesEvent.newBuilder();
            parser.merge(vesEventJson, builder);
            var event = builder.build().getEvent();
            publishEvent(site, ne, event);

            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (InvalidProtocolBufferException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            // } catch (InterruptedException | ExecutionException e) {
            //     log.error("Unable to send message to topic {}", topic, e);
            //     return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }
    }

    private <T extends GeneratedMessageV3.Builder<T>> Message parseJsonToProtobuf(
        String json, T builder
    ) throws InvalidProtocolBufferException {
        parser.merge(json, builder);
        return builder.build();
    }

    @PostMapping({"/v1.0/event/sync", "/eventListener/v7/eventBatch"})
    public ResponseEntity<Void> collectFaultBatch(@RequestBody String vesEventList, ServerHttpRequest request) {
        try {
            var ip = getClientIp(request);
            var ne = neCache.get(ip);
            var site = siteCache.get(ne.getSiteId());
            var builder = DCAE.VesEventList.newBuilder();
            parser.merge(vesEventList, builder);

            var list = builder.build()
                .getEventList()
                .stream()
                .map(event -> publishEvent(site, ne, event))
                .collect(Collectors.toList());

            handleResultList(list);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (InvalidProtocolBufferException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Unable to send message to topic", e);
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }
    }

    private void handleResultList(
        List<CompletableFuture<SendResult<EventKey.Key, DCAE.Event>>> list
    ) throws ExecutionException, InterruptedException {
        for (var f : list) {
            f.get();
        }
    }

    private CompletableFuture<SendResult<EventKey.Key, DCAE.Event>> publishEvent(NeSite site, NE ne, DCAE.Event event) {
        var header = event.getCommonEventHeader();
        var key = getKey(site, ne, header);
        var topic = header.getDomain();

        log.info("Publishing msg to '{}' topic", topic);
        return kafkaTemplate.send(topic, key, event).completable();
    }

    private static EventKey.Key getKey(NeSite site, NE ne, DCAE.Event.CommonEventHeader header) {
        return EventKey.Key.newBuilder()
            .setNeId(ne.getId())
            .setSiteId(ne.getSiteId())
            .setEventCode(header.getEventId())
            .setInitialInstant(header.getStartEpochMicrosec())
            .build();
    }
}
