package com.viettel.ems.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
public class EffectiveTimeRange {

    @JsonProperty("from_date")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate fromDate;

    @JsonProperty("to_date")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate toDate;

    @JsonProperty("time_range")
    private List<TimeRange> timeRangeList;

    @Data
    public static class TimeRange {
        @JsonDeserialize(using = LocalTimeDeserializer.class)
        private LocalTime from;

        @JsonDeserialize(using = LocalTimeDeserializer.class)
        private LocalTime to;
    }

    public boolean contains(LocalDateTime dateTime) {
        var date = dateTime.toLocalDate();
        var time = dateTime.toLocalTime();
        if (date.compareTo(fromDate) >= 0 && date.compareTo(toDate) <= 0) {
            for (var range : timeRangeList) {
                if (time.compareTo(range.from) >= 0 && time.compareTo(range.to) <= 0) {
                    return true;
                }
            }
        }
        return false;
    }
}
