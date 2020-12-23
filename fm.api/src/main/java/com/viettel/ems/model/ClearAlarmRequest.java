package com.viettel.ems.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ClearAlarmRequest extends Pagination {
    @JsonProperty("user_id")
    private String user;

    @JsonProperty("alarms")
    private List<EventKey> keyList;
}
