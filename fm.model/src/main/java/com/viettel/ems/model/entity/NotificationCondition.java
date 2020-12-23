package com.viettel.ems.model.entity;

import com.viettel.utils.condition.Column;
import com.viettel.utils.condition.Table;
import lombok.*;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("user_notify_condition")
public class NotificationCondition {
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "condition")
    private String condition;

    @Component
    public static class Mapper implements RowMapper<NotificationCondition> {
        @Override
        public NotificationCondition mapRow(ResultSet rs, int rowNum) throws SQLException {
            return NotificationCondition.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .condition(rs.getString("condition"))
                .build();
        }
    }
}
