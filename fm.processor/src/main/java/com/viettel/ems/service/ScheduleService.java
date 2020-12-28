package com.viettel.ems.service;

import com.viettel.ems.scheduler.CommandRunner;
import com.viettel.ems.scheduler.ScriptConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static com.viettel.ems.utils.StringUtils.split;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    @Value("${ems.cm.address}")
    private String cmAddress;
    private final NamedParameterJdbcTemplate jdbc;
    private final ThreadPoolTaskScheduler scheduler;

    @PostConstruct
    public void postConstruct() {
        jdbc.query(
            "SELECT sc.id AS id, cron_exp, command_set, rule_id, name, ne_list, device_type, stop_on_error, commands "
                + "FROM schedule_config sc JOIN command_set cs ON sc.command_set = cs.id;",
            rs -> {
                var trigger = new CronTrigger(rs.getString("cron_exp"));
                var script = ScriptConfig.builder()
                    // .id(rs.getInt("id"))
                    .neIpList(split(rs.getString("ne_list"), ","))
                    .deviceType(rs.getString("device_type"))
                    .stopOnError(rs.getBoolean("stop_on_error"))
                    .commandList(split(rs.getString("command_set"), "\n"))
                    .build();;
                scheduler.schedule(new CommandRunner(cmAddress, script), trigger);
            });
    }
}
