package com.viettel.ems.model.entity;

import com.viettel.utils.condition.Column;
import com.viettel.utils.condition.Table;
import lombok.*;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("event_category")
public class EventCategory {

    @Column(name = "id")
    private int id;

    @Column(name = "site_id")
    private int siteId;

    @Column(name = "name")
    private String name;

    @Column(name = "code")
    private String code;

    @Column(name = "enable")
    private boolean enable;

    @Column(name = "message")
    private String message;

    public static class Key {

    }

    @Component
    public static class Mapper implements RowMapper<EventCategory> {
        @Override
        public EventCategory mapRow(ResultSet rs, int rowNum) throws SQLException {
            return EventCategory.builder()
                .id(rs.getInt("id"))
                .siteId(rs.getInt("site_id"))
                .name(rs.getString("name"))
                .enable(rs.getBoolean("enable"))
                .message(rs.getString("message"))
                .build();
        }
    }
}
