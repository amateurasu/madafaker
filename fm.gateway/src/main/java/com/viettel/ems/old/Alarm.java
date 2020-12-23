package com.viettel.ems.old;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.viettel.ems.model.Severity;
import lombok.*;
import org.onap.ves.DCAE;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Alarm {

    //region Properties
    @JsonProperty("ne_type")
    private String neType;

    @JsonProperty("ne_id")
    private int neId;

    @JsonProperty("ne")
    private String ne;

    @JsonProperty("alarm_id")
    private String alarmId;

    @JsonProperty("level")
    private int severity;

    @JsonProperty("event_type")
    private int eventType;

    @JsonProperty("location")
    private String location;

    @JsonProperty("probable_cause")
    private String probableCause;

    @JsonProperty("keygen")
    private String keyGen;

    @JsonProperty("trigger_time")
    private String triggerTime;

    @JsonProperty("additional_info")
    private String additionalInfo;

    @JsonProperty("mask")
    private boolean mask;
    //endregion

    public DCAE.Event toValueProto() {
        return DCAE.Event.newBuilder()
            .setCommonEventHeader(DCAE.Event.CommonEventHeader.newBuilder()
                .setEventId(alarmId)
                .setLastEpochMicrosec(Long.parseLong(triggerTime))
                .setSourceName(ne)
                .build())
            .setFaultFields(DCAE.Event.FaultFields.newBuilder()
                .setEventSeverity(Severity.fromCode(severity).name())
                .setAlarmInterfaceA(location)
                .setSpecificProblem(probableCause)
                .putAlarmAdditionalInformation("info", additionalInfo)
                .build())
            .build();
    }
}

