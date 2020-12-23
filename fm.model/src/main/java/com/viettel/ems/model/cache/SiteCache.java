package com.viettel.ems.model.cache;

import com.viettel.ems.model.entity.NeSite;
import com.viettel.utils.condition.Table;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Table("ne_site")
@AllArgsConstructor
public class SiteCache extends ConcurrentHashMap<Integer, NeSite> {

    private final JdbcTemplate jdbc;
    private final NamedParameterJdbcTemplate namedJdbc;
    private final NeSite.Mapper mapper;

    @Override
    public NeSite get(Object key) {
        var site = super.get(key);
        return site == null ? fetchFromDb((int) key) : site;
    }

    public SiteCache prefetch(int... keys) {
        namedJdbc.query("SELECT * FROM ne_site WHERE id IN (:id)", Map.of("ip", keys), rs -> {
            var ne = mapper.mapRow(rs, 0);
            if (ne != null) {
                put(rs.getInt("id"), ne);
            }
        });
        return this;
    }

    private NeSite fetchFromDb(int id) {
        var result = jdbc.queryForObject("SELECT * FROM ne_site nt WHERE id = ?", mapper, id);
        if (result == null) return null;
        put(id, result);
        return result;
    }
}
