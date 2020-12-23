package com.viettel.utils.condition.logic;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.utils.condition.Query;
import com.viettel.utils.condition.reflection.ObjectReflect;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public abstract class ListCondition extends ObjectCondition {

    protected final List<?> list;

    public ListCondition(String fieldExp, List<?> list) {
        super(fieldExp);
        this.list = list;
    }

    public static ListCondition of(String field, String op, List<?> value) throws JsonParseException {
        switch (op) {
            case "in": return new IN(field, value);
            case "nin": return new NIN(field, value);
            default: throw new JsonParseException(null, "Expect list comparison expression but got '" + op + '\'');
        }
    }

    public static class IN extends ListCondition {
        public IN(String field, List<?> value) {
            super(field, value);
        }

        public <T> boolean evaluate(ObjectReflect<T> reflect) throws Exception {
            T value = reflect.getValue(expression);
            return list.contains(value);
        }

        @Override
        public <T> Query<T> buildQuery(Query<T> query) throws Exception {
            return query.field(expression).append(" IN (").prepare(list).append(')').addParams(list);
        }

        @Override
        public JsonNode toJson(ObjectMapper mapper) {
            var arrayNode = mapper.createArrayNode();
            arrayNode.add(expression);
            arrayNode.add("in");
            arrayNode.addPOJO(list);
            return arrayNode;
        }
    }

    public static class NIN extends ListCondition {
        public NIN(String field, List<?> value) {
            super(field, value);
        }

        public <T> boolean evaluate(ObjectReflect<T> reflect) throws Exception {
            T value = reflect.getValue(expression);
            return !list.contains(value);
        }

        @Override
        public <T> Query<T> buildQuery(Query<T> query) throws Exception {
            return query.field(expression).append(" NOT IN (").prepare(list).append(')').addParams(list);
        }

        @Override
        public JsonNode toJson(ObjectMapper mapper) {
            var arrayNode = mapper.createArrayNode();
            arrayNode.add(expression);
            arrayNode.add("nin");
            arrayNode.addPOJO(list);
            return arrayNode;
        }
    }
}
