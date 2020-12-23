package com.viettel.utils.condition.reflection;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
public class ReflectTest {
    private static class A {
        @Ann(ten = "abc")
        private int a;

        @Ann(ten = "def")
        private int b;
    }

    @Test
    public void testAnnotation() {
        var reflect = new Reflect(A.class);
        var fields = reflect.fields;
        assertNotNull(fields);
        assertEquals(fields.size(), 2);
        try {
            assertEquals(fields.get("abc"), A.class.getDeclaredField("a"));
            assertEquals(fields.get("def"), A.class.getDeclaredField("b"));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

    }
}
