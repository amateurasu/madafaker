package com.viettel.ems.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.temporal.ChronoUnit;

@Data
public class RateLimit {
    @JsonProperty("value")
    private final int limit;

    @JsonProperty("time_limit")
    private final int time;

    @JsonProperty("time_unit")
    private final ChronoUnit timeUnit;
}
