package com.viettel.ems.api;

import com.viettel.ems.model.AckAlarmRequest;
import com.viettel.ems.model.ClearAlarmRequest;
import com.viettel.ems.model.FilterRequest;
import com.viettel.ems.model.entity.Fault;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1.0/alarm")
public class AlarmController_v1_0 {

    private final JdbcTemplate jdbc;
    private final Fault.Mapper faultMapper;

    @PostMapping
    public Map<String, ?> getCurrentAlarms(@RequestBody FilterRequest filter) {
        return fetchAlarm(filter, "");
    }

    @PostMapping("/current")
    public Map<String, ?> getCurrentAlarm(@RequestBody FilterRequest filter) {
        return fetchAlarm(filter, "cleared_by IS NULL AND isolated = FALSE AND event_id IS NOT NULL");
    }

    @PostMapping("/history")
    public Map<String, ?> getAlarmHistory(@RequestBody FilterRequest filter) {
        return fetchAlarm(filter, "cleared_by IS NOT NULL AND event_id IS NOT NULL");
    }

    @PostMapping("/isolated")
    public Map<String, ?> getIsolatedAlarm(@RequestBody FilterRequest filter) {

        return fetchAlarm(filter, "cleared_by IS NULL AND isolated = TRUE AND event_id IS NOT NULL");
    }

    @PostMapping("/unknown")
    public Map<String, ?> getUnknownAlarm(@RequestBody FilterRequest filter) {
        return fetchAlarm(filter, "event_id IS NULL");
    }

    @PostMapping("/clear")
    public Map<String, ?> clearAlarms(@RequestBody ClearAlarmRequest request) {
        try {
            var cleared = transactionalClear(request);
            log.info("request: {},\n\tresult: {}", cleared);
            if (cleared == null) {
                return Map.of("status", 1, "data", "Couldn't clear alarm(s)!");
            } else {
                return Map.of("status", 0, "data", "Clear alarm(s) successfully!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("status", 1, "message", "Clear alarm fail: Internal error, check FM log for details");
        }
    }

    @PostMapping("/ack")
    public Map<String, ?> handleAlarmAck(@RequestBody AckAlarmRequest request) {
        try {
            var acked = transactionalAck(request);
            log.info("request: {},\n\tresult: {}", acked);
            if (acked == null) {
                return Map.of("code", 1, "message", "Couldn't ack alarm(s)!");
            } else {
                return Map.of("code", 0, "message", "Acked/Unack alarm(s) successfully!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("code", 1, "message", "Ack/Unack alarm fail: Internal error, check FM log for details");
        }
    }

    private Map<String, ?> fetchAlarm(FilterRequest filter, String predicate) {
        try {
            var page = filter.getPage();
            var size = filter.getPageSize();
            size = size == 0 ? 50 : size;

            var query = filter.getCondition()
                .buildQuery(Fault.class, predicate)
                .append(" LIMIT " + size + " OFFSET " + page * size);

            var params = query.getParamArray();
            var sql = query.getSql();
            log.info("Query: \n\t{}\n\t{}", sql, params);
            var faults = jdbc.query(sql, params, faultMapper);
            return Map.of("page", page, "page_size", size, "data", faults, "data_size", faults.size());
        } catch (Exception e) {
            log.error("Couldn't fetch alarm", e);
            return Map.of("message", String.valueOf(e.getMessage()));
        }
    }

    @Transactional
    public int[] transactionalClear(ClearAlarmRequest request) {
        //language=MySQL
        final var sql = "UPDATE fault SET cleared_by = ?, clear_instant = ? WHERE ne_id = ? AND event_id = ? "
            + "AND initial_instant = ?";
        final var user = request.getUser();
        final var keys = request.getKeyList();
        final var now = new Timestamp(System.currentTimeMillis());
        return jdbc.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(@NonNull PreparedStatement ps, int i) throws SQLException {
                var key = keys.get(i);
                ps.setString(1, user);
                ps.setTimestamp(2, now);
                ps.setInt(3, key.getNeId());
                ps.setInt(4, key.getEventId());
                ps.setTimestamp(5, Timestamp.valueOf(key.getInitialInstant()));
            }

            @Override
            public int getBatchSize() {
                return keys.size();
            }
        });
    }

    @Transactional
    public int[] transactionalAck(AckAlarmRequest request) {
        //language=MySQL
        final var sql = "UPDATE fault SET acked_by = ?, ack_instant = ? WHERE ne_id = ? AND event_id = ? "
            + "AND initial_instant = ?";
        final var user = request.getUser();
        final var acked = request.isAcked();
        final var keys = request.getKeyList();
        final var now = new Timestamp(System.currentTimeMillis());
        return jdbc.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(@NonNull PreparedStatement ps, int i) throws SQLException {
                var key = keys.get(i);
                ps.setString(1, acked ? user : null);
                ps.setTimestamp(2, acked ? now : null);
                ps.setInt(3, key.getNeId());
                ps.setInt(4, key.getEventId());
                ps.setTimestamp(5, Timestamp.valueOf(key.getInitialInstant()));
            }

            @Override
            public int getBatchSize() {
                return keys.size();
            }
        });
    }
}
