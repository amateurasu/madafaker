package com.viettel.ems.service.filter;

import com.viettel.ems.model.cache.NeCache;
import com.viettel.ems.model.entity.NE;
import com.viettel.ems.model.event.Threshold;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class RateLimitFilter implements WebFilter {

    private final NeCache neCache;
    private final ApplicationEventPublisher eventPublisher;
    private final NamedParameterJdbcTemplate namedJdbc;

    private final List<Double> systemThreshold;

    @PostConstruct
    public void fetchConfig() {
        var KEYS = new String[] {
            "fm.threshold.ne.critical",
            "fm.threshold.ne.major",
            "fm.threshold.ne.minor",
            "fm.threshold.ne.warning",
            "fm.threshold.system.critical",
            "fm.threshold.system.major",
            "fm.threshold.system.minor",
            "fm.threshold.system.value",
            "fm.threshold.system.warning",
            };

        var params = Map.of("keys", KEYS);
        var hashMap = namedJdbc.query("SELECT `key`, `value` FROM param_code WHERE `key` IN (:keys)", params, (rs) -> {
            var map = new HashMap<String, String>();
            while (rs.next()) {
                var key = rs.getString("key");
                var value = rs.getString("value");
                log.info("key {}, value {}", key, value);
                map.put(key, value);
            }
            return map;
        });
        log.info("{}", hashMap);
    }

    @Override
    @NonNull
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain filterChain) {
        var request = exchange.getRequest();
        var remoteAddress = request.getRemoteAddress();

        var response = exchange.getResponse();
        if (remoteAddress == null) {
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return response.setComplete();
        }

        var ip = remoteAddress.getAddress().getHostAddress();
        var ne = neCache.get(ip);
        if (ne == null) {
            log.error("Cannot find NE with IP {} in the db", ip);
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return response.setComplete();
        }

        log.info("id {} ({}): addr {} - {}", ne.getId(), ne.getName(), ip, remoteAddress);
        request.getHeaders().forEach((key, value) -> System.out.format("\tKey: %s, Value: %s\n", key, value));

        if (!checkRateLimit(ne)) {
            log.error("NE with IP {} has sent too many request!", ip);
            response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            return response.setComplete();
        }
        return filterChain.filter(exchange);
    }

    private boolean checkRateLimit(NE ne) {
        var bucket = ne.getBucket();
        var bw = bucket.getConfiguration().getBandwidths()[0];

        var consumptionProbe = bucket.tryConsumeAndReturnRemaining(1);
        var remaining = consumptionProbe.getRemainingTokens();
        var capacity = bw.getCapacity();
        var consumed = 100 - ((double) remaining / capacity) * 100;
        if (consumed > 80) {
            log.warn("Rate limit reached {}% ({}/{})", consumed, remaining, capacity);
            eventPublisher.publishEvent(new Threshold.Remain(this, remaining, capacity));
        }

        return consumptionProbe.isConsumed();
    }
}
