package com.viettel.ems.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class TimeUtils {
    public static LocalDateTime fromMicros(String micros, String timezone) {
        var m = Long.parseLong(micros);
        return fromMicros(m, timezone);
    }

    public static LocalDateTime fromMicros(long micros, String timezone) {
        var mega = 1_000_000L;
        var sec = micros / mega;
        var nano = micros % mega * 1_000;

        var instant = Instant.ofEpochSecond(sec, nano);
        var tz = ZoneId.of(timezone);
        return LocalDateTime.ofInstant(instant, tz);
    }
}
