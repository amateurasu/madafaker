package com.viettel.ems.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class DbUtils {

    public static LocalDateTime getDateTime(ResultSet rs, String field) throws SQLException {
        var timestamp = rs.getTimestamp(field);
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
