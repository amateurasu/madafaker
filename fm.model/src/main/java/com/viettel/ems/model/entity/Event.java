package com.viettel.ems.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.viettel.ems.utils.TimeUtils;
import com.viettel.utils.condition.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.onap.ves.DCAE;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.viettel.ems.utils.DbUtils.getDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @JsonProperty("site_id")
    @Column(name = "site_id")
    protected int siteId;

    @JsonProperty("site_name")
    protected String siteName;

    @JsonProperty("ne_id")
    @Column(name = "ne_id")
    protected int neId;

    @JsonProperty("event_id")
    @Column(name = "event_id")
    protected int eventId;

    @JsonProperty("event_name")
    @Column(name = "event_name")
    protected String eventName;

    @JsonProperty("event_code")
    @Column(name = "event_code")
    protected String eventCode;

    @JsonProperty("event_type")
    @Column(name = "event_type")
    protected String eventType;

    @JsonProperty("nfc_naming_code")
    @Column(name = "nfc_naming_code")
    protected String nfcNamingCode;

    @JsonProperty("nf_naming_code")
    @Column(name = "nf_naming_code")
    protected String nfNamingCode;

    @JsonProperty("nf_vendor_name")
    @Column(name = "nf_vendor_name")
    protected String nfVendorName;

    @JsonProperty("priority")
    @Column(name = "priority")
    protected String priority;

    @JsonProperty("reporting_entity_id")
    @Column(name = "reporting_entity_id")
    protected String reportingEntityId;

    @JsonProperty("reporting_entity_name")
    @Column(name = "reporting_entity_name")
    protected String reportingEntityName;

    @JsonProperty("sequence")
    @Column(name = "sequence")
    protected int sequence;

    @JsonProperty("source_id")
    @Column(name = "source_id")
    protected String sourceId;

    @JsonProperty("source_name")
    @Column(name = "source_name")
    protected String sourceName;

    @JsonProperty("trigger_instant")
    @Column(name = "trigger_instant")
    protected LocalDateTime triggerInstant;

    @JsonProperty("initial_instant")
    @Column(name = "initial_instant")
    protected LocalDateTime initialInstant;

    @JsonProperty("stnd_defined_namespace")
    @Column(name = "stnd_defined_namespace")
    protected String stndDefinedNamespace;

    @JsonProperty("version")
    @Column(name = "version")
    protected String version;

    @JsonProperty("internal_header_fields")
    @Column(name = "internal_header_fields")
    protected String internalHeaderFields;

    @JsonProperty("ves_event_listener_version")
    @Column(name = "ves_event_listener_version")
    protected String vesEventListenerVersion;

    @JsonProperty("insert_instant")
    @Column(name = "insert_instant")
    private LocalDateTime insertInstant;

    @JsonProperty("update_instant")
    @Column(name = "update_instant")
    private LocalDateTime updateInstant;

    @JsonIgnore
    @Column(name = "trigger_date", db = "DATE(trigger_instant)")
    protected LocalDate triggerDate;

    @JsonIgnore
    @Column(name = "initial_date", db = "DATE(initial_instant)")
    protected LocalDate initialDate;

    @JsonIgnore
    @Column(name = "insert_date", db = "DATE(insert_instant)")
    private LocalDate insertDate;

    @JsonIgnore
    @Column(name = "update_date", db = "DATE(update_instant)")
    private LocalDate updateDate;

    @JsonIgnore
    @Column(name = "trigger_time", db = "TIME(trigger_instant)")
    protected LocalTime triggerTime;

    @JsonIgnore
    @Column(name = "initial_time", db = "TIME(initial_instant)")
    protected LocalTime initialTime;

    @JsonIgnore
    @Column(name = "insert_time", db = "TIME(insert_instant)")
    private LocalTime insertTime;

    @JsonIgnore
    @Column(name = "update_time", db = "TIME(update_instant)")
    private LocalTime updateTime;

    @Column(name = "isolated")
    private boolean isolated;

    @JsonProperty("isolation_history")
    @Column(name = "isolation_history")
    private String isolationHistory;

    public static <T extends Event> T fromProto(DCAE.Event evt, T event) {
        var header = evt.getCommonEventHeader();
        var tz = header.getTimeZoneOffset();
        var trigger = TimeUtils.fromMicros(header.getLastEpochMicrosec(), tz);
        var initial = TimeUtils.fromMicros(header.getStartEpochMicrosec(), tz);

        event.setEventCode(header.getEventId());
        event.setEventName(header.getEventName());
        event.setEventType(header.getEventType());
        event.setNfcNamingCode(header.getNfcNamingCode());
        event.setNfNamingCode(header.getNfNamingCode());
        event.setNfVendorName(header.getNfVendorName());
        event.setPriority(header.getPriority().toString());

        event.setReportingEntityId(header.getReportingEntityId());
        event.setReportingEntityName(header.getReportingEntityName());

        event.setSequence(header.getSequence());
        event.setSourceId(header.getSourceId());
        event.setSourceName(header.getSourceName());

        event.setTriggerInstant(trigger);
        event.setTriggerDate(trigger.toLocalDate());
        event.setTriggerTime(trigger.toLocalTime());

        event.setInitialInstant(initial);
        event.setInitialDate(initial.toLocalDate());
        event.setInitialTime(initial.toLocalTime());

        event.setStndDefinedNamespace(header.getStndDefinedNamespace());
        event.setVersion(header.getVersion());
        event.setVesEventListenerVersion(header.getVesEventListenerVersion());

        return event;
    }

    public static <T extends Event> T fromResultSet(ResultSet rs, T event) throws SQLException {
        var siteId = rs.getInt("site_id");
        event.setSiteId(siteId);
        event.setNeId(rs.getInt("ne_id"));
        event.setEventId(rs.getInt("event_id"));
        event.setEventCode(rs.getString("event_code"));
        event.setSequence(rs.getInt("sequence"));
        event.setIsolated(rs.getBoolean("isolated"));
        event.setIsolationHistory(rs.getString("isolation_history"));
        event.setInitialInstant(getDateTime(rs, "initial_instant"));
        event.setTriggerInstant(getDateTime(rs, "trigger_instant"));
        event.setInsertInstant(getDateTime(rs, "insert_instant"));
        event.setUpdateInstant(getDateTime(rs, "update_instant"));
        event.setEventName(rs.getString("event_name"));
        event.setEventType(rs.getString("event_type"));
        event.setInternalHeaderFields(rs.getString("internal_header_fields"));
        event.setNfcNamingCode(rs.getString("nfc_naming_code"));
        event.setNfNamingCode(rs.getString("nf_naming_code"));
        event.setNfVendorName(rs.getString("nf_vendor_name"));

        return event;
    }
}
