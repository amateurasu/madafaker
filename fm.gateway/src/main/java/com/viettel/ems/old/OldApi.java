package com.viettel.ems.old;

import com.viettel.ems.model.cache.NeCache;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.onap.ves.DCAE;
import org.onap.ves.EventKey;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.viettel.ems.utils.ServerUtils.getClientIp;

@Data
@Slf4j
@RestController
public class OldApi {

    private final NeCache neCache;
    private final JdbcTemplate jdbc;
    private final KafkaTemplate<EventKey.Key, DCAE.Event> kafkaTemplate;

    @PostMapping("/alarmreport")
    public ResponseEntity<Object> receiveAlarm(@RequestBody List<Alarm> alarms, ServerHttpRequest request) {
        var neIp = getClientIp(request);

        try {
            var ne = neCache.get(neIp);
            var value = alarms.get(0).toValueProto();
            var key = EventKey.Key.newBuilder()
                .setNeId(ne.getId())
                .setSiteId(ne.getSiteId())
                .setInitialInstant(value.getCommonEventHeader().getLastEpochMicrosec())
                .build();

            var future = kafkaTemplate.send(getTopic(), key, value).completable();
            var result = future.get();
            log.info("Sent message=[{}] with offset=[{}]", getTopic(), result.getRecordMetadata().offset());

            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }
    }

    @PostMapping("/eventreport")
    public ResponseEntity<Object> receiveEvent(@RequestBody List<Event> events, ServerHttpRequest request) {
        var key = EventKey.Key.newBuilder()

            .build();
        var value = events.get(0).toValueProto();

        try {

            var future = kafkaTemplate.send(getTopic(), key, value).completable();
            var result = future.get();
            log.info("Sent message=[{}] with offset=[{}]", getTopic(), result.getRecordMetadata().offset());

            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }
    }

    private String getTopic() {
        return "fault";
    }

    private String getTopic(String systemType, String siteName, String type) {
        return String.format("EMS_%s_%s_%s", systemType, siteName, type);
    }
}
