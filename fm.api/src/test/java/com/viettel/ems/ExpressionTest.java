package com.viettel.ems;

import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.List;

public class ExpressionTest {

    static class Inner extends ExpressionTest {
        @Getter
        private final String trigger_time = "2007-12-03T10:15:30";
        @Getter
        private final String time = "2007-12-03T10:15:30";
    }

    @Test
    public void test() {
        var inner = new Inner();
        System.out.println("${3} + ${4} = ${3+4} at ${inner.trigger_time}");

        ExpressionParser parser = new SpelExpressionParser();
        Expression exp = parser.parseExpression("#{list(trigger_time,time)}", ParserContext.TEMPLATE_EXPRESSION);

        var message = exp.getValue(new StandardEvaluationContext(inner), List.class);
        System.out.println(message);
    }

    public List<?> list(Object... s) {
        return List.of(s);
    }
}
