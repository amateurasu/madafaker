package com.viettel.utils.condition.logic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.utils.condition.ICondition;
import com.viettel.utils.condition.Query;
import com.viettel.utils.condition.reflection.ObjectReflect;
import com.viettel.utils.condition.reflection.Reflect;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class LogicGroupCondition implements ICondition {
    private final String name;
    private final List<LogicWrapper> logics;
    private final int size;
    private final int start;

    public LogicGroupCondition(String name, List<LogicWrapper> logics) {
        this.name = name;
        this.logics = logics;
        this.size = logics.size();

        int i = size;
        while (--i >= 0) {
            if ("start".equals(logics.get(i).getOperator())) {
                break;
            }
        }
        start = i;
    }

    public static ICondition of(String name, List<LogicWrapper> list) {
        return new LogicGroupCondition(name, list);
    }

    @Override
    public <T> boolean evaluate(ObjectReflect<T> reflect) throws Exception {
        if (start < 0) return true;

        boolean result = false;
        for (var i = start; i < size; i++) {
            var logic = logics.get(i);
            switch (logic.getOperator()) {
                case "start":
                    result = logic.evaluate(reflect);
                    break;
                case "and":
                    if (!result) return false;
                    result = logic.evaluate(reflect);
                    break;
                case "or":
                    if (result) return true;
                    result = logic.evaluate(reflect);
                    break;
                default:
                    throw new Exception("Unknown logic operator");
            }
        }
        return result;
    }

    @Override
    public <T> Query<T> buildQuery(Query<T> query) throws Exception {
        if (start < 0) return query.append("true ");

        for (int i = start; i < size; i++){
            var logic = logics.get(i);
            switch (logic.getOperator()) {
                case "start":
                    logic.buildQuery(query.append('('));
                    break;
                case "and":
                    logic.buildQuery(query.append(" AND "));
                    break;
                case "or":
                    logic.buildQuery(query.append(" OR "));
                    break;
            }
        }
        return query.append(')');
    }

    @Override
    public boolean validate(Reflect reflect) {
        var result = true;
        // return logics.stream().allMatch(logic -> logic.validate(reflect));
        for (var logic : logics) {
            result &= logic.validate(reflect);
            if (!result) System.out.println(logic);
        }
        return result;
    }

    @Override
    public JsonNode toJson(ObjectMapper mapper) {
        var node = mapper.createObjectNode();
        var array = mapper.createArrayNode();
        logics.forEach(logic -> array.add(logic.toJson(mapper)));
        node.put("name", name);
        node.set("exp", array);
        return node;
    }

    public String toString() {
        try {
            var mapper = new ObjectMapper();
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(toJson(mapper));
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
