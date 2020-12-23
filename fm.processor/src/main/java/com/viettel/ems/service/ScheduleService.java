package com.viettel.ems.service;

import com.viettel.ems.scheduler.CommandRunner;
import com.viettel.ems.scheduler.ScheduleConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final NamedParameterJdbcTemplate jdbc;
    private final ThreadPoolTaskScheduler scheduler;
    private final ScheduleConfig.Mapper mapper;

    @PostConstruct
    public void postConstruct() {
        var scheduleConfigs = jdbc.query("SELECT * FROM schedule_config", mapper);

        var trigger = new CronTrigger("10 * * * * ?");
        scheduler.schedule(new CommandRunner("Specific time, 3 Seconds from now"), trigger);
    }
}
