package com.viettel.ems.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.ems.model.entity.Fault;
import com.viettel.ems.model.entity.Notification;
import com.viettel.ems.model.event.NewMessageAppEvent;
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
    public void storeEvent(NewMessageAppEvent e) {
        var event = e.getEvent();
        if (event.getInitialInstant() == null) {
            if (event instanceof Fault) {
                storeVhtEvent((Fault) event);
            } else if (event instanceof Notification) {
                storeVhtEvent((Notification) event);
            }
        } else {
            if (event instanceof Fault) {
                storeVesEvent((Fault) event);
            } else if (event instanceof Notification) {
                storeVesEvent((Notification) event);
            }
        }
    }

    private void storeVesEvent(Notification n) {

    }

    private void storeVesEvent(Fault f) {
        var sql = "INSERT INTO fault (ne_id, event_code, severity, ems_severity, sequence, interface, "
            + "source_type, specific_problem, initial_instant, trigger_instant, event_id, event_name, event_type, "
            + "nfc_naming_code, nf_naming_code, nf_vendor_name, priority, reporting_entity_id, reporting_entity_name, "
            + "source_id, source_name, stnd_defined_namespace, version, ves_event_listener_version, alarm_condition, "
            + "event_category, fault_fields_version, vf_status, additional_info) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE "
            + "sequence = IF(VALUES(sequence) > sequence, VALUES(sequence), sequence), "
            + "severity = IF(VALUES(sequence) > sequence, VALUES(severity), severity), "
            + "trigger_instant = IF(VALUES(sequence) > sequence, VALUES(trigger_instant), NOW())";

        var initial = Timestamp.valueOf(f.getInitialInstant());
        var trigger = Timestamp.valueOf(f.getTriggerInstant());

        var mapper = new ObjectMapper();
        try {
            var additionalInfo = mapper.writeValueAsString(f.getAdditionalInfo());
            System.out.println(mapper.writeValueAsString(f));
            var updated = jdbc.update(sql, f.getNeId(), f.getEventId(), f.getSeverity(), f.getSeverity(),
                f.getSequence(), f.getInterfaceA(), f.getSourceType(), f.getSpecificProblem(), initial, trigger,
                f.getEventId(), f.getEventName(), f.getEventType(), f.getNfcNamingCode(), f.getNfNamingCode(),
                f.getNfVendorName(), f.getPriority(), f.getReportingEntityId(), f.getReportingEntityName(),
                f.getSourceId(), f.getSourceName(), f.getStndDefinedNamespace(), f.getVersion(),
                f.getVesEventListenerVersion(), f.getCondition(), f.getCategory(), f.getFaultFieldsVersion(),
                f.getVfStatus(), additionalInfo);
            if (updated > 0) {
                log.info("Updated fault message for NE {} ()", f.getSourceName());
            } else {
                log.info("Couldn't update fault message for NE {}, ", "");
            }
        } catch (JsonProcessingException jsonProcessingException) {
            jsonProcessingException.printStackTrace();
        }
    }

    private void storeVhtEvent(Fault f) {
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

    private void storeVhtEvent(Notification n) {
        var select = "SELECT cleared_by FROM fault WHERE ne_id = ? AND event_id = ? AND trigger_instant = ("
            + "SELECT MAX(trigger_instant) FROM fault f1 WHERE ne_id = ? AND event_id = ? GROUP BY ne_id, event_id)";
        var cleared = jdbc.queryForObject(select, (rs, rowNum) -> rs.getString("cleared_by"), n.getNeId(),
            n.getEventId(), n.getNeId(), n.getEventId());

        if (cleared == null) {
            var insert = "INSERT INTO event(ne_id, event_id, event_code, severity, ems_severity, "
                + "isolated, isolation_history, initial_instant, trigger_instant, "
                + "event_name, sequence) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1)";
            jdbc.update(insert, n.getNeId(), n.getEventId(), n.getEventCode(), n.isIsolated(), n.getIsolationHistory(),
                Timestamp.valueOf(n.getTriggerInstant()), Timestamp.valueOf(n.getTriggerInstant()), n.getEventName(),
                n.getAdditionalInfo());
        } else {
            var update =
                "UPDATE event SET severity = ?, trigger_instant = ?, isolated = ? WHERE ne_id = ? AND event_id = ? AND "
                    + "initial_instant = ?";
            jdbc.update(update, Timestamp.valueOf(n.getTriggerInstant()), n.isIsolated());
        }
    }
}
