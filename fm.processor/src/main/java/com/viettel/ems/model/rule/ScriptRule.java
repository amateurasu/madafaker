package com.viettel.ems.model.rule;

import com.viettel.ems.model.EventHandlePayload;
import com.viettel.ems.model.entity.Event;
import com.viettel.ems.scheduler.ScriptConfig;
import lombok.Getter;
import lombok.Setter;

public class ScriptRule extends Rule {

    @Getter
    @Setter
    private ScriptConfig config;

    @Override
    public void handle(Event event, EventHandlePayload payload) throws Exception {
        if (condition.evaluate(event)) {
            payload.getCommands().add(config);
        }
    }
}
