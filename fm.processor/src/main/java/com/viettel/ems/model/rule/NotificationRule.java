package com.viettel.ems.model.rule;

import com.viettel.ems.model.EventHandlePayload;
import com.viettel.ems.model.entity.Event;
import com.viettel.ems.model.entity.UserNotification;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class NotificationRule extends Rule {

    @Getter
    protected final List<UserNotification> notifications = new ArrayList<>();

    @Override
    public void handle(Event event, EventHandlePayload payload) throws Exception {
        if (!condition.evaluate(event)) return;
        var emails = payload.getEmails();
        var phones = payload.getPhones();

        notifications.forEach(notification -> {
            if (!notification.isActive()) return;
            switch (notification.getMethod()) {
                case "SMS":
                    phones.add(notification.getPhone());
                    break;
                case "MAIL":
                    emails.add(notification.getMail());
                    break;
                case "ALL":
                    emails.add(notification.getMail());
                    phones.add(notification.getPhone());
            }
        });
    }

    public void add(UserNotification notification) {
        notifications.add(notification);
    }
}
