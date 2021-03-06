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
@Table("user_notification")
public class UserNotification {

    @Column(name = "condition_id")
    private int conditionId;

    @Column(name = "user_id")
    public String userId;

    @Column(name = "active")
    private boolean active;

    @Column(name = "method")
    private String method;

    private String phone;
    private String mail;
}
