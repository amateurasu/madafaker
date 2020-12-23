package com.viettel.ems.kafka;

import com.google.protobuf.DynamicMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.viettel.ems.model.cache.RuleCache;
import com.viettel.ems.model.entity.Fault;
import com.viettel.ems.model.event.FaultAppEvent;
import com.viettel.utils.condition.ICondition;
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

    @KafkaListener(topics = "${message.topic.name}")
    public void listenFaultMessage(
        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) DynamicMessage key,
        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
        @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long ts,
        @Payload DynamicMessage message
    ) throws InvalidProtocolBufferException {
        var time = new Date().getTime();
        var event = DCAE.VesEvent.parseFrom(message.toByteArray());
        var eventKey = EventKey.Key.parseFrom(key.toByteArray());
        log.info("Received ({}:{}): {} ({}, lag: {})", topic, partition, eventKey, new Timestamp(ts), time - ts);

        var fault = Fault.fromProto(event);
        // fault.setNeId(eventKey.getSourceId());
        checkRule(fault);
    }

    public void checkRule(Fault fault) {
        ruleCache.forEach((id, rule) -> {
            var condition = rule.getCondition();
            try {
                if (condition.evaluate(fault)) {
                    rule.handle(fault);
                }
                eventPublisher.publishEvent(new FaultAppEvent(this, fault));
            } catch (Exception e) {
                log.error("Error checking rule on fault", e);
            }
        });
    }
}
