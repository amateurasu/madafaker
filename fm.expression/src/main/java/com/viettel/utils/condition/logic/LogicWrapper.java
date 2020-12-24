package com.viettel.utils.condition.logic;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.utils.condition.Condition;
import com.viettel.utils.condition.Query;
import com.viettel.utils.condition.reflection.ObjectReflect;
import com.viettel.utils.condition.reflection.Reflect;
import lombok.Data;

@Data
public class LogicWrapper implements Condition {
    private final String operator;
    private final Condition condition;

    public static LogicWrapper of(String name, Condition condition) throws JsonParseException {
        switch (name) {
            case "start":
            case "and":
            case "or":
                return new LogicWrapper(name, condition);
            default:
                throw new JsonParseException(null, "Expect string comparison expression but got '" + name + '\'');
        }
    }

    @Override
    public <T> boolean evaluate(ObjectReflect<T> reflect) throws Exception {
        return condition.evaluate(reflect);
    }

    @Override
    public <T> Query<T> buildQuery(Query<T> query) throws Exception {
        return condition.buildQuery(query);
    }

    @Override
    public boolean validate(Reflect reflect) {
        return condition.validate(reflect);
    }

    @Override
    public JsonNode toJson(ObjectMapper mapper) {
        return mapper.createObjectNode().set(operator, condition.toJson(mapper));
    }

    @Override
    public String toString() {
        return operator.toUpperCase() + " " + condition;
    }
}
