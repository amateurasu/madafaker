package com.viettel.ems.kafka;

import com.google.protobuf.DynamicMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.viettel.ems.model.EventHandlePayload;
import com.viettel.ems.model.cache.RuleCache;
import com.viettel.ems.model.entity.Event;
import com.viettel.ems.model.entity.Fault;
import com.viettel.ems.model.entity.Notification;
import com.viettel.ems.model.event.NewMessageAppEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onap.ves.DCAE;
import org.onap.ves.EventKey;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventListener {

    private final ApplicationEventPublisher eventPublisher;
    private final RuleCache ruleCache;

    @KafkaListener(topics = "${message.topic.fault}")
    public void listenFaultMessage(
        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) DynamicMessage kafkaKey,
        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
        @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long ts,
        @Payload DynamicMessage message
    ) throws InvalidProtocolBufferException {
        var time = new Date().getTime();
        var key = EventKey.Key.parseFrom(kafkaKey.toByteArray());
        var event = DCAE.Event.parseFrom(message.toByteArray());
        log.info("Received ({}:{}): {} ({}, lag: {})", topic, partition, key, new Timestamp(ts), time - ts);

        checkRule(key, Fault.fromProto(event));
    }

    @KafkaListener(topics = "${message.topic.notification}")
    public void listenNotificationMessage(
        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) DynamicMessage kafkaKey,
        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
        @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long ts,
        @Payload DynamicMessage message
    ) throws InvalidProtocolBufferException {
        var time = new Date().getTime();
        var key = EventKey.Key.parseFrom(kafkaKey.toByteArray());
        var event = DCAE.Event.parseFrom(message.toByteArray());
        log.info("Received ({}:{}): {} ({}, lag: {})", topic, partition, key, new Timestamp(ts), time - ts);

        checkRule(key, Notification.fromProto(event));
    }

    public void checkRule(EventKey.Key key, Event event) {
        event.setNeId(key.getNeId());
        event.setSiteId(key.getSiteId());
        event.setEventCode(key.getEventCode());

        var payload = new EventHandlePayload();
        ruleCache.forEach((id, rule) -> {
            var condition = rule.getCondition();
            try {
                if (condition.evaluate(event)) {
                    rule.handle(event, payload);
                }
            } catch (Exception e) {
                log.error("Error checking rule on event", e);
            }
        });
        eventPublisher.publishEvent(new NewMessageAppEvent(this, event, payload));
    }
}
