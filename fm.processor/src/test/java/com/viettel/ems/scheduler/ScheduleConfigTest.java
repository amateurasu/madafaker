package com.viettel.ems.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ScheduleConfigTest {
    @Test
    public void test() throws JsonProcessingException {
        var config = ScheduleConfig.builder()
            .commandList(List.of("xyz"))
            .neIpList(List.of("abc", "def"))
            .stopOnError(true)
            .cronExpression("0 0 6 * * *")
            .build();
        var mapper = new ObjectMapper();
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(config));
    }
}
