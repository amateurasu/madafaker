package com.viettel.utils.expression;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomTest {
    @Test
    public void date() {
        var builder = new ExpressionBuilder("date(trigger_time)")
            .function("date", values -> values[0])
            .variable("trigger_time")
            .build();
        var date = builder.setVariable("trigger_time", 100);
        var evaluate = date.evaluate();
        System.out.println(evaluate);
        assertEquals(100, evaluate);
    }
}
