package com.viettel.ems.model.cache;

import com.viettel.ems.model.entity.EventCategory;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
@AllArgsConstructor
public class EventCategoryCache extends ConcurrentHashMap<Integer, EventCategory> {

    private final JdbcTemplate jdbc;
    private final EventCategory.Mapper mapper;

    // @Override
    // public EventCategory get(Object id) {
        // if (!(id instanceof EventCategory.Key)) {
        //     throw new IllegalArgumentException("Event Category's ID must be an instance of EventCategory.Key!");
        // }
        //
        // var event = super.get(id);
        // if (event == null) {
        //     var key = (EventCategory.Key) id;
        //     return fetchFromDb(key, key.getId(), key.getNeTypeId());
        // }
        // return event;
    // }

    // private EventCategory fetchFromDb(EventCategory.Key id, int eventId, int neType) {
        // var evt = jdbc.queryForObject("SELECT * FROM event_category WHERE id = ? AND site_id = ?", mapper, eventId,
        //     neType);
        // if (evt == null) return null;
        // put(id, evt);
        // return evt;
    // }
}
