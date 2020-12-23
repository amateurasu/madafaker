package com.viettel.ems.model.event;

import com.viettel.ems.model.entity.Notification;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class NotificationAppEvent extends ApplicationEvent {

    private final Notification notification;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with which the event is associated (never
     *     {@code null})
     * @param notification detail
     */
    public NotificationAppEvent(Object source, Notification notification) {
        super(source);
        this.notification = notification;
    }
}
