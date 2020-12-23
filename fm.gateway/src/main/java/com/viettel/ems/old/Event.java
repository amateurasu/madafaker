package com.viettel.ems.old;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.onap.ves.DCAE;

@Data
public class Event {

    @JsonProperty("id")
    private int id;

    @JsonProperty("ne_type_id")
    private int neTypeId;
    
    @JsonProperty("ne_type")
    private String neType;

    @JsonProperty("ne_id")
    private int neId;

    @JsonProperty
    private String ne;

    @JsonProperty("event_id")
    private int eventId;

    @JsonProperty("event_name")
    private String eventName;

    @JsonProperty
    private String message;

    @JsonProperty
    private String location;

    @JsonProperty("probable_cause")
    private String probableCause;

    @JsonProperty("create_time")
    private String createTime;

    @JsonProperty("insert_time")
    private String insertTime;

    public DCAE.Event toValueProto() {
        return DCAE.Event.newBuilder()

            .build();
    }
}
