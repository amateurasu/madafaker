package com.viettel.ems.model.rule;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.ems.model.EffectiveTimeRange;
import com.viettel.ems.model.EventHandlePayload;
import com.viettel.ems.model.entity.Event;
import com.viettel.utils.condition.Condition;
import lombok.Builder;
import lombok.Data;

@Data
public abstract class Rule {

    protected int id;
    protected String name;
    protected String type;
    protected boolean enabled;

    protected String effectiveTime;
    protected EffectiveTimeRange timeRange;

    protected String conditionJson;
    protected Condition condition;

    public void setConditionJson(String json) {
        this.conditionJson = json;
        try {
            condition = new ObjectMapper().readValue(json, new TypeReference<>() { });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void setEffectiveTime(String json) {

    }

    public abstract void handle(Event event, EventHandlePayload payload) throws Exception;
}
