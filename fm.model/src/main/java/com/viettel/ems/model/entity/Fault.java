package com.viettel.ems.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.utils.condition.Column;
import com.viettel.utils.condition.Table;
import lombok.*;
import org.onap.ves.DCAE;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import static com.viettel.ems.utils.DbUtils.getDateTime;

@Data
@Builder
@Table("fault")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Fault extends Event {

    @JsonProperty("additional_info")
    @Column(name = "additional_info")
    private Map<String, String> additionalInfo; // No

    @JsonProperty("condition")
    @Column(name = "condition")
    private String condition; // Yes

    @JsonProperty("interface")
    @Column(name = "location", db = "interface")
    private String interfaceA; // No

    @JsonProperty("category")
    @Column(name = "category")
    private String category; // No

    @JsonProperty("severity")
    @Column(name = "severity")
    private String severity; // Yes

    @JsonProperty("ems_severity")
    @Column(name = "ems_severity")
    private String emsSeverity; // Yes

    @JsonProperty("source_type")
    @Column(name = "source_type")
    private String sourceType; // Yes

    @JsonProperty("fault_fields_version")
    @Column(name = "fault_fields_version")
    private String faultFieldsVersion; // Yes

    @JsonProperty("specific_problem")
    @Column(name = "specific_problem")
    private String specificProblem; // Yes

    @JsonProperty("vf_status")
    @Column(name = "vf_status")
    private String vfStatus; // Yes

    @JsonProperty("acked_by")
    @Column(name = "acked_by")
    private String ackedBy; // No

    @JsonProperty("ack_instant")
    @Column(name = "ack_instant")
    private LocalDateTime ackInstant;

    @JsonIgnore
    @Column(name = "ack_date", db = "DATE(ack_instant)")
    private LocalDate ackDate;

    @JsonIgnore
    @Column(name = "ack_time", db = "TIME(ack_instant)")
    private LocalTime ackTime;

    public static Fault fromProto(DCAE.Event event) {
        var fault = Event.fromProto(event, new Fault());
        var fields = event.getFaultFields();

        fault.setAdditionalInfo(fields.getAlarmAdditionalInformationMap());
        fault.setCondition(fields.getAlarmCondition());
        fault.setInterfaceA(fields.getAlarmInterfaceA());
        fault.setCategory(fields.getEventCategory());
        fault.setSeverity(fields.getEventSeverity());
        fault.setSourceType(fields.getEventSourceType());
        fault.setVersion(fields.getFaultFieldsVersion());
        fault.setSpecificProblem(fields.getSpecificProblem());
        fault.setVfStatus(fields.getVfStatus());

        return fault;
    }

    @Component
    public static class Mapper implements RowMapper<Fault> {
        @Override
        public Fault mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
            var fault = Event.fromResultSet(rs, new Fault());

            fault.setSeverity(rs.getString("severity"));
            fault.setEmsSeverity(rs.getString("ems_severity"));
            fault.setInterfaceA(rs.getString("interface"));
            fault.setSourceType(rs.getString("source_type"));
            fault.setSpecificProblem(rs.getString("specific_problem"));
            fault.setAckedBy(rs.getString("acked_by"));
            fault.setAckInstant(getDateTime(rs, "ack_instant"));

            fault.setPriority(rs.getString("priority"));
            fault.setReportingEntityId(rs.getString("reporting_entity_id"));
            fault.setReportingEntityName(rs.getString("reporting_entity_name"));
            fault.setSourceId(rs.getString("source_id"));
            fault.setSourceName(rs.getString("source_name"));
            fault.setStndDefinedNamespace(rs.getString("stnd_defined_namespace"));
            fault.setVersion(rs.getString("version"));
            fault.setVesEventListenerVersion(rs.getString("ves_event_listener_version"));
            fault.setCondition(rs.getString("alarm_condition"));
            fault.setCategory(rs.getString("event_category"));
            fault.setFaultFieldsVersion(rs.getString("fault_fields_version"));
            fault.setVfStatus(rs.getString("vf_status"));

            try {
                fault.setAdditionalInfo(new ObjectMapper().readValue(rs.getString("additional_info"), new TypeReference<HashMap<String, String>>() {}));
            } catch (JsonProcessingException ignored) { }

            return fault;
        }
    }
}
