package com.viettel.ems.model.event;

import com.viettel.ems.model.entity.Fault;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.lang.NonNull;

public class FaultAppEvent extends ApplicationEvent {

    @Getter
    private final Fault fault;

    public FaultAppEvent(@NonNull Object source, Fault fault) {
        super(source);
        this.fault = fault;
    }
}
