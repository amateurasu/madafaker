package com.viettel.ems.model.cache;

import com.viettel.ems.model.entity.UserNotification;
import com.viettel.ems.model.rule.*;
import com.viettel.ems.scheduler.ScriptConfig;
import com.viettel.ems.utils.AsyncJdbc;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.viettel.ems.utils.StringUtils.split;

@Slf4j
@Component
@AllArgsConstructor
public class RuleCache extends ConcurrentHashMap<Integer, Rule> {

    private final JdbcTemplate jdbc;
    private final AsyncJdbc async;

    @PostConstruct
    public void loadRules() {
        var notification = new ArrayList<Integer>();
        var script = new ArrayList<Integer>();

        jdbc.query("SELECT id, name, type, enabled, effective_time, `condition` FROM event_rule", rs -> {
            var id = rs.getInt("id");
            var type = rs.getString("type");
            Rule rule;

            switch (type) {
                case "notification":
                    notification.add(id);
                    rule = new NotificationRule();
                    break;
                case "isolation":
                    notification.add(id);
                    rule = new IsolationRule();
                    break;
                case "script":
                    script.add(id);
                    rule = new ScriptRule();
                    break;
                default:
                    return;
            }
            rule.setId(id);
            rule.setName(rs.getString("name"));
            rule.setType(type);
            rule.setEnabled(rs.getBoolean("enabled"));
            rule.setEffectiveTime(rs.getString("effective_time"));
            rule.setConditionJson(rs.getString("condition"));
            put(id, rule);
        });
        loadNotificationRuleDetails(notification);
        loadScriptRuleDetails(script);
        log.info("loaded {} rule(s)", size());
    }

    private void loadNotificationRuleDetails(List<Integer> notification) {
        if (notification == null || notification.isEmpty()) return;

        async.query("SELECT rule_id, user_id, active, method, email, phone_number FROM user_notification un "
                + "JOIN user u ON u.id = un.user_id WHERE rule_id IN (:list);", Map.of("list", notification),
            (rs, rowNum) -> {
                var ruleId = rs.getInt("rule_id");
                var un = UserNotification.builder()
                    .userId(rs.getString("user_id"))
                    .active(rs.getBoolean("active"))
                    .method(rs.getString("method"))
                    .conditionId(rs.getInt("condition_id"))
                    .phone(rs.getString("phone_number"))
                    .mail(rs.getString("email"))
                    .build();
                ((NotificationRule) get(ruleId)).add(un);
                return null;
            });
    }

    private void loadScriptRuleDetails(List<Integer> script) {
        if (script == null || script.isEmpty()) return;

        async.query(
            "SELECT id, rule_id, name, ne_list, device_type, stop_on_error, commands FROM command_set cs "
                + "WHERE rule_id IN (:list);",
            Map.of("list", script), (rs, rowNum) -> {
                var ruleId = rs.getInt("rule_id");
                var un = ScriptConfig.builder()
                    .id(rs.getInt("id"))
                    .neIpList(split(rs.getString("ne_list"), ","))
                    .deviceType(rs.getString("device_type"))
                    .stopOnError(rs.getBoolean("stop_on_error"))
                    .commandList(split(rs.getString("commands"), "\n"))
                    .build();
                ((ScriptRule) get(ruleId)).setConfig(un);
                return null;
            });
    }
}
