package com.viettel.ems.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.ems.model.entity.Event;
import com.viettel.utils.condition.Column;
import com.viettel.utils.condition.Condition;
import com.viettel.utils.condition.Table;
import lombok.Data;

@Data
@Table("event_rule")
public class Rule {

    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "effective_time")
    private String effectiveTime;

    @Column(name = "condition")
    private String conditionJson;

    private EffectiveTimeRange timeRange;
    private Condition condition;

    public void setConditionJson(String json) {
        this.conditionJson = json;
        try {
             condition = new ObjectMapper().readValue(json, new TypeReference<>() { });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void handle(Event event) {

    }
}
