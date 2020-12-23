package com.viettel.ems.scheduler;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.viettel.ems.utils.ScheduleConfigSerializer;
import lombok.Builder;
import lombok.Data;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static com.viettel.ems.utils.StringUtils.split;

@Data
@Builder
@JsonSerialize(using = ScheduleConfigSerializer.class)
public class ScheduleConfig {

    private int id;
    private String cronExpression;
    private List<String> commandList;
    private List<String> neIpList;
    private String deviceType;
    private boolean stopOnError;

    @Component
    public static class Mapper implements RowMapper<ScheduleConfig> {
        @Override
        public ScheduleConfig mapRow(ResultSet rs, int rowNum) throws SQLException {
            return ScheduleConfig.builder()
                .id(rs.getInt("id"))
                .cronExpression(rs.getString("cron_exp"))
                .neIpList(split(rs.getString("ne_list"), ","))
                .deviceType(rs.getString("device_type"))
                .stopOnError(rs.getBoolean("stop_on_error"))
                .commandList(split(rs.getString("command_list"), "\n"))
                .build();
        }
    }
}
