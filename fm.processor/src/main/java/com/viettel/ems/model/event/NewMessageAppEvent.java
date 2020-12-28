package com.viettel.ems.model.event;

import com.viettel.ems.model.EventHandlePayload;
import com.viettel.ems.model.entity.Event;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.lang.NonNull;

public class NewMessageAppEvent extends ApplicationEvent {

    @Getter
    private final Event event;

    @Getter
    private final EventHandlePayload payload;

    public NewMessageAppEvent(@NonNull Object source, Event event, EventHandlePayload payload) {
        super(source);
        this.event = event;
        this.payload = payload;
    }
}
