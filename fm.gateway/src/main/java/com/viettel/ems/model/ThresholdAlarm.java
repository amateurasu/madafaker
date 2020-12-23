package com.viettel.ems.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ThresholdAlarm {

    @JsonProperty("percentage")
    private double percentage;

    @JsonProperty("alarm_id")
    private int alarmId;
}
