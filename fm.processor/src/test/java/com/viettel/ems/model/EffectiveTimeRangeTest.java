package com.viettel.ems.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class EffectiveTimeRangeTest {

    private EffectiveTimeRange timeRange;

    @BeforeEach
    public void readJson() throws IOException {
        var resource = new String(EffectiveTimeRangeTest.class.getResourceAsStream("time_range.json").readAllBytes());
        var mapper = new ObjectMapper();
        timeRange = mapper.readValue(resource, new TypeReference<>() { });
    }

    @Test
    public void testParsingJson() throws IOException {
        Assertions.assertNotNull(timeRange);
    }

    @Test
    public void testEvaluating() throws IOException {
        var dateTime = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        var evaluate = timeRange.contains(dateTime);
        System.out.println(evaluate);
    }

}
