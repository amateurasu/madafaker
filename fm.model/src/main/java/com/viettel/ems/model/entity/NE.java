package com.viettel.ems.model.entity;

import com.viettel.utils.condition.Column;
import com.viettel.utils.condition.Table;
import io.github.bucket4j.local.LocalBucket;
import lombok.*;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("ne")
public class NE {

    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "ip")
    private String ip;

    @Column(name = "site_id")
    private int siteId;

    private NeSite site;

    private LocalBucket bucket;

    @Component
    public static class Mapper implements RowMapper<NE> {
        @Override
        public NE mapRow(ResultSet rs, int rowNum) throws SQLException {
            return NE.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .ip(rs.getString("ip"))
                .siteId(rs.getInt("site_id"))
                .build();
        }
    }
}
