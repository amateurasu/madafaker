package com.viettel.ems.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventKey {
    @JsonProperty("ne_id")
    private int neId;

    @JsonProperty("event_id")
    private int eventId;

    @JsonProperty("initial_instant")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime initialInstant;
}
