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
public class ScriptConfig {

    private int id;

    @JsonProperty("list_command")
    private List<String> commandList;

    @JsonProperty("list_ne")
    private List<String> neIpList;

    @JsonProperty("device_type")
    private String deviceType;

    @JsonProperty("stop_on_error")
    private boolean stopOnError;
}
