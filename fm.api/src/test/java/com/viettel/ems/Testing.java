package com.viettel.ems;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.ems.model.entity.Fault;
import com.viettel.ems.model.entity.Notification;
import org.junit.jupiter.api.Test;

public class Testing {

    @Test
    public void testNotification() throws JsonProcessingException {
        var mapper = new ObjectMapper();
        var notification = new Notification();
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(notification));
    }

    @Test
    public void test() throws JsonProcessingException {
        var mapper = new ObjectMapper();
        var fault = new Fault();
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(fault));
    }
}
