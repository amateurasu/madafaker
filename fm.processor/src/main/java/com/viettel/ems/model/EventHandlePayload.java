package com.viettel.ems.model;

import com.viettel.ems.scheduler.ScriptConfig;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class EventHandlePayload {

    private String subject;
    private String mailContent;
    private String smsContent;
    private Set<String> phones;
    private Set<String> emails;

    private List<ScriptConfig> commands;
}
