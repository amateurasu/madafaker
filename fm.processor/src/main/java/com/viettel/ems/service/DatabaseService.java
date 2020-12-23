package com.viettel.ems.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.ems.model.event.FaultAppEvent;
import com.viettel.ems.model.event.NotificationAppEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseService {

    private final JdbcTemplate jdbc;

    @Async
    @EventListener
    public void storeEvent(FaultAppEvent e) {
        var f = e.getFault();
        if (f.getInitialInstant() == null) {
            storeVhtEvent(e);
        } else {
            storeVesEvent(e);
        }
    }

    private void storeVesEvent(FaultAppEvent e) {
        var sql = "INSERT INTO fault (ne_id, event_code, severity, ems_severity, sequence, interface, "
            + "source_type, specific_problem, initial_instant, trigger_instant, event_id, event_name, event_type, "
            + "nfc_naming_code, nf_naming_code, nf_vendor_name, priority, reporting_entity_id, reporting_entity_name, "
            + "source_id, source_name, stnd_defined_namespace, version, ves_event_listener_version, alarm_condition, "
            + "event_category, fault_fields_version, vf_status, additional_info) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE "
            + "sequence = IF(VALUES(sequence) > sequence, VALUES(sequence), sequence), "
            + "severity = IF(VALUES(sequence) > sequence AND VALUES(severity) != 'NORMAL', VALUES(severity), severity),"
            + "trigger_instant = IF(VALUES(sequence) > sequence, VALUES(severity), severity),"
            + "cleared_by = IF(VALUES(severity) = 'NORMAL')";

        // true, "isolation_history",
        var f = e.getFault();
        var initial = Timestamp.valueOf(f.getInitialInstant());
        var trigger = Timestamp.valueOf(f.getTriggerInstant());
        log.info("initial {}, trigger {}", initial, trigger);
        var mapper = new ObjectMapper();
        try {
            var additionalInfo = mapper.writeValueAsString(f.getAdditionalInfo());
            var params = new Object[] {f.getNeId(), f.getEventId(), f.getSeverity(), f.getSeverity(), f.getSequence()
                , f.getInterfaceA(), f.getSourceType(), f.getSpecificProblem(), initial, trigger, f.getEventId(),
                f.getEventName(), f.getEventType(), f.getNfcNamingCode(), f.getNfNamingCode(), f.getNfVendorName(),
                f.getPriority(), f.getReportingEntityId(), f.getReportingEntityName(), f.getSourceId(),
                f.getSourceName(), f.getStndDefinedNamespace(), f.getVersion(), f.getVesEventListenerVersion(),
                f.getCondition(), f.getCategory(), f.getFaultFieldsVersion(), f.getVfStatus(), additionalInfo};

            var updated = jdbc.update(sql, params);
            if (updated > 0) {
                log.info("Updated fault message for NE {} ()", f.getSourceName());
            } else {
                log.info("Couldn't update fault message for NE {}, ", "");
            }
        } catch (JsonProcessingException jsonProcessingException) {
            jsonProcessingException.printStackTrace();
        }
    }

    private void storeVhtEvent(FaultAppEvent e) {
        var f = e.getFault();

        var select = "SELECT cleared_by FROM fault WHERE ne_id = ? AND event_id = ? AND trigger_instant = ("
            + "SELECT MAX(trigger_instant) FROM fault f1 WHERE ne_id = ? AND event_id = ? GROUP BY ne_id, event_id)";
        var cleared = jdbc.queryForObject(select, (rs, rowNum) -> rs.getString("cleared_by"), f.getNeId(),
            f.getEventId(), f.getNeId(), f.getEventId());

        if (cleared == null) {
            var insert = "INSERT INTO fault(ne_id, event_id, event_code, severity, ems_severity, interface,"
                + " source_type, specific_problem, isolated, isolation_history, initial_instant, trigger_instant, "
                + "event_name, additional_info, sequence) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1)";
            jdbc.update(insert, f.getNeId(), f.getEventId(), f.getEventCode(), f.getSeverity(), f.getSeverity(),
                f.getInterfaceA(), f.getSourceType(), f.getSpecificProblem(), f.isIsolated(), f.getIsolationHistory(),
                Timestamp.valueOf(f.getTriggerInstant()), Timestamp.valueOf(f.getTriggerInstant()), f.getEventName(),
                f.getAdditionalInfo());
        } else {
            var update =
                "UPDATE fault SET severity = ?, trigger_instant = ?, isolated = ? WHERE ne_id = ? AND event_id = ? AND "
                    + "initial_instant = ?";
            jdbc.update(update, f.getSeverity(), Timestamp.valueOf(f.getTriggerInstant()), f.isIsolated());
        }
    }

    @Async
    @EventListener
    public void storeEvent(NotificationAppEvent e) {
        var n = e.getNotification();
    }
}
