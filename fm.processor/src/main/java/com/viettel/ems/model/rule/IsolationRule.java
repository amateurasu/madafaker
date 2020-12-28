package com.viettel.ems.model.rule;

import com.viettel.ems.model.EventHandlePayload;
import com.viettel.ems.model.entity.Event;

public class IsolationRule extends NotificationRule {
    @Override
    public void handle(Event event, EventHandlePayload payload) throws Exception {
        super.handle(event, payload);
        if (condition.evaluate(event)) {
            event.setIsolated(true);
        }
    }
}
