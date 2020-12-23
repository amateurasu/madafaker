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
@Table("user")
public class User {

    @Column(name = "id")
    private String id;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @Component
    public static class Mapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return User.builder()
                .id(rs.getString("id"))
                .fullName(rs.getString("full_name"))
                .phoneNumber(rs.getString("phone_number"))
                .email(rs.getString("email"))
                .build();
        }
    }
}
