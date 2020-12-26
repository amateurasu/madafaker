package com.viettel.ems.api;

import com.viettel.ems.model.FilterRequest;
import com.viettel.ems.model.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * The type User controller.
 *
 * @author Le Quoc Trung
 */
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/event")
public class EventController_v1_0 {

    private final JdbcTemplate jdbc;
    private final Notification.Mapper eventMapper;

    @PostMapping(path = "/history")
    public Map<String, ?> getEventHistory(@RequestBody FilterRequest filter) {
        return fetchNotification(filter, "isolated = FALSE");
    }

    @PostMapping(path = "/isolated")
    public Map<String, ?> getIsolatedEvents(@RequestBody FilterRequest filter) {
        return fetchNotification(filter, "isolated = TRUE");
    }

    private Map<String, ?> fetchNotification(FilterRequest filter, String predicate) {
        try {
            var page = filter.getPage();
            var size = filter.getPageSize();
            size = size == 0 ? 50 : size;

            var query = filter.getCondition()
                .buildQuery(Notification.class, predicate)
                .append(" LIMIT " + size + " OFFSET " + page * size);

            var params = query.getParamArray();
            var sql = query.getSql();
            log.info("Query: \n\t{}\n\t{}", sql, params);
            var faults = jdbc.query(sql, params, eventMapper);
            return Map.of("page", page, "page_size", size, "data", faults);
        } catch (Exception e) {
            log.error("Couldn't fetch event", e);
            return Map.of("message", e.getMessage());
        }
    }
}
