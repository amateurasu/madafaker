package com.viettel.utils.condition;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class TimeTest {
    @Test
    public void testConvert() {
        var dateTime = LocalDateTime.now();
        var timestamp = Timestamp.valueOf(dateTime);
        var fromTimestamp = timestamp.toLocalDateTime();
        log.info("dateTime: {}", dateTime);
        log.info("timestamp: {}", timestamp);

        log.info("{}", dateTime.toLocalDate());
        log.info("{}", dateTime.toLocalTime());
    }

    @Test
    public void test_LocalTime() {
        var time = LocalTime.parse("15:10:00.000000000");
        var time2 = LocalTime.parse("15:10:00.000000000");
        log.info("time 1 = {}; time 2 = {}", time, time2);
        assertEquals(time, time2);
    }

    @Test
    public void test_now() {
        var time = LocalTime.now();
        var time1 = LocalTime.now();
        log.info("time 1 = {}; time 2 = {}", time, time1);
        assertEquals(time, time1);
    }
}
