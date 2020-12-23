package com.viettel.ems.model.cache;

import com.viettel.ems.model.entity.NE;
import com.viettel.ems.model.entity.NeSite;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@AllArgsConstructor
public class NeCache extends ConcurrentHashMap<String, NE> {

    private final JdbcTemplate jdbc;
    private final NamedParameterJdbcTemplate namedJdbc;
    private final NE.Mapper neMapper;

    @Override
    public NE get(Object key) {
        if (!(key instanceof String)) {
            throw new IllegalArgumentException("Key need to be an IP string!");
        }

        var ne = super.get(key);
        return ne == null ? fetchFromDb(key.toString()) : ne;
    }

    public NeCache prefetch(String... keys) {
        namedJdbc.query("SELECT * FROM ne WHERE ip IN (:ip)", Map.of("ip", keys), rs -> {
            var ne = neMapper.mapRow(rs, 0);
            if (ne != null) {
                put(rs.getString("ip"), ne);
            }
        });
        return this;
    }

    private NE fetchFromDb(String key) {
        var sql = "SELECT ne.id AS id, ne.name AS name, ip, site_id, ns.id AS ns_id, ns.name AS ns_name, "
            + "system_type_id, request_limit, time_limit, time_unit FROM ne JOIN ne_site ns ON ns.id = ne.site_id "
            + "WHERE ip = ?";
        var result = jdbc.queryForObject(sql, (rs, num) -> {
            var ne = neMapper.mapRow(rs, 0);
            var requestLimit = rs.getInt("request_limit");
            var timeLimit = rs.getInt("time_limit");
            var timeUnit = rs.getString("time_unit");
            var site = NeSite.builder()
                .id(rs.getInt("ns_id"))
                .name(rs.getString("ns_name"))
                .systemTypeId(rs.getInt("system_type_id"))
                .build();
            var refill = Refill.greedy(requestLimit, Duration.of(timeLimit, ChronoUnit.valueOf(timeUnit)));
            var bandwidth = Bandwidth.classic(requestLimit, refill);
            var bucket = Bucket4j.builder().addLimit(bandwidth).build();
            if (ne != null) {
                ne.setBucket(bucket);
                ne.setSite(site);
            }
            return ne;
        }, key);
        if (result == null) return null;
        put(key, result);
        return result;
    }
}
