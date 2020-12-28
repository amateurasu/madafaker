package com.viettel.ems.model.rule;

import com.viettel.ems.model.EventHandlePayload;
import com.viettel.ems.model.entity.Event;

import java.util.List;

public class NotificationRule extends Rule {
    // protected List<C>

    @Override
    public void handle(Event event, EventHandlePayload payload) throws Exception {
        if (condition.evaluate(event)) {

        }
    }
}
