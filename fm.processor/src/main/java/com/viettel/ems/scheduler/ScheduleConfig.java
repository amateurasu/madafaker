package com.viettel.ems.scheduler;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class ScheduleConfig {

    private int id;

    @JsonProperty("list_command")
    private List<String> commandList;

    @JsonProperty("list_ne")
    private List<String> neIpList;

    @JsonProperty("device_type")
    private String deviceType;

    @JsonProperty("stop_on_error")
    private boolean stopOnError;

    @Component
    public static class Mapper implements RowMapper<ScheduleConfig> {
        @Override
        public ScheduleConfig mapRow(ResultSet rs, int rowNum) throws SQLException {
            return ScheduleConfig.builder()
                .id(rs.getInt("id"))
                .neIpList(split(rs.getString("ne_list"), ","))
                .deviceType(rs.getString("device_type"))
                .stopOnError(rs.getBoolean("stop_on_error"))
                .commandList(split(rs.getString("command_list"), "\n"))
                .build();
        }
    }
}
