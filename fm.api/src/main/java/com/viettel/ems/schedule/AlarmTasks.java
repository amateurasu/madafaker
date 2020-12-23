package com.viettel.ems.schedule;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
// @Component
@AllArgsConstructor
public class AlarmTasks {

    private final JdbcTemplate jdbc;

    @Scheduled(fixedRate = 300_000, initialDelay = 10_000)
    public void scheduleCheckAlarmTimeout() {
        try {
            var updated = jdbc.update(
                "INSERT INTO alarm_history(ne_id, category_id, message, severity, repeat_count, probable_cause, "
                    + "location, trigger_time, clear_time, insert_time, mask) SELECT ne_id, category_id, message, "
                    + "severity, repeat_count, probable_cause, location, trigger_time, update_time, insert_time, mask "
                    + "FROM alarm_current ac INNER JOIN alarm_category a ON ac.category_id = a.id "
                    + "WHERE a.timeout > 0 AND NOW() - ac.trigger_time >= a.timeout");
            if (updated < 1) {
                log.info("There's no timeout alarm in 'alarm_current'");
                return;
            }
            var deleted = jdbc.update(
                "DELETE ac FROM alarm_current ac INNER JOIN alarm_category a ON ac.category_id = a.id "
                    + "WHERE a.timeout > 0 AND NOW() - ac.trigger_time >= a.timeout");
            if (updated == deleted) {
                log.info("Completely archived {} record(s) from 'alarm_current' to 'alarm_history'", updated);
            } else {
                log.warn("Archived {}/{} record(s) from 'alarm_current' to 'alarm_history'", updated, deleted);
            }
        } catch (DataAccessException e) {
            log.error("Could not finish deleting timeout alarm", e);
        }
    }

    @Scheduled(fixedRate = 30_000, initialDelay = 5_000)
    public void scheduleCheckEventReportFail() {
        // try {
        //     List<EventReport> eventReportList = eventReportRepository.findAll();
        //     if (eventReportList.size() <= 0) return;
        //
        //     for (EventReport eventReport : eventReportList) {
        //         TimeZone tz = TimeZone.getDefault();
        //         long currentTime = System.currentTimeMillis() + tz.getRawOffset();
        //         String message = eventReport.getMessage();
        //         Timestamp triggerTime = eventReport.getTriggerTime();
        //         long delta = currentTime - triggerTime.getTime();
        //         if (delta > 10000 && delta < 200000) {
        //             log.info("Reprocess event {} {}", eventReport.getId(), eventReport.getMessage());
        //             ObjectMapper mapper = new ObjectMapper();
        //             List<Map<String, String>> mapEvent = mapper.readValue(message, new TypeReference<>() { });
        //             alarmCurrentController.processEventReport(mapEvent, triggerTime, eventReport.getId());
        //         }
        //     }
        // } catch (Exception e) {
        //     log.error("CheckEventReportFail not finished", e);
        // }
    }
}
