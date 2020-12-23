package com.viettel.ems.model.entity;

import com.viettel.utils.condition.Column;
import com.viettel.utils.condition.Table;
import lombok.Builder;
import lombok.Data;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.temporal.ChronoUnit;

@Data
@Builder
@Table("ne_site")
public class NeSite {

    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "system_type_id")
    private int systemTypeId;

    @Column(name = "request_limit")
    private int requestLimit;

    @Column(name = "time_limit")
    private int timeLimit;

    @Column(name = "time_unit")
    private ChronoUnit timeUnit;

    @Component
    public static class Mapper implements RowMapper<NeSite> {
        @Override
        public NeSite mapRow(ResultSet rs, int rowNum) throws SQLException {
            return NeSite.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .systemTypeId(rs.getInt("system_type_id"))
                .requestLimit(rs.getInt("request_limit"))
                .timeLimit(rs.getInt("time_limit"))
                .timeUnit(ChronoUnit.valueOf(rs.getString("time_unit")))
                .build();
        }
    }
}
