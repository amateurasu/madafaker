package com.viettel.utils.condition.logic;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.utils.condition.ICondition;
import com.viettel.utils.condition.Query;
import com.viettel.utils.condition.reflection.ObjectReflect;
import com.viettel.utils.condition.reflection.Reflect;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Getter
@Setter
@Slf4j
@AllArgsConstructor
public abstract class LogicCondition implements ICondition {

    protected List<ICondition> conditions;

    public static LogicCondition of(String op, List<ICondition> conditions) throws JsonParseException {
        switch (op) {
            case "and": return new AND(conditions);
            case "or": return new OR(conditions);
            default:
                throw new JsonParseException(null, "Expect logical expression but got '" + op + "'");
        }
    }

    @Override
    public boolean validate(Reflect reflect) {
        for (int i = 0, size = conditions.size(); i < size; i++) {
            var op = conditions.get(i);
            if (!op.validate(reflect)) return false;
        }
        return true;
    }

    public static class AND extends LogicCondition {
        public AND(List<ICondition> conditions) {
            super(conditions);
        }

        @Override
        public <T> boolean evaluate(ObjectReflect<T> reflect) throws Exception {
            for (int i = 0, size = conditions.size(); i < size; i++) {
                var op = conditions.get(i);
                if (!op.evaluate(reflect)) {
                    log.debug("false at: {}", op);
                    return false;
                }
            }
            return true;
        }

        @Override
        public <T> Query<T> buildQuery(Query<T> query) throws Exception {
            //fixme
            var op = conditions.get(0);
            var length = conditions.size();
            query.append('(');
            op.buildQuery(query);
            for (int i = 1; i < length; i++) {
                query.append(" AND ");
                conditions.get(i).buildQuery(query).append(')');
            }
            return query;
        }

        @Override
        public JsonNode toJson(ObjectMapper mapper) {
            var node = mapper.createObjectNode();
            var array = mapper.createArrayNode();
            array.addPOJO(conditions);
            node.set("$and", array);
            return node;
        }
    }

    public static class OR extends LogicCondition {
        public OR(List<ICondition> conditions) {
            super(conditions);
        }

        @Override
        public <T> boolean evaluate(ObjectReflect<T> reflect) throws Exception {
            for (int i = 0, size = conditions.size(); i < size; i++) {
                var op = conditions.get(i);
                if (op != null && op.evaluate(reflect)) return true;
            }
            return false;
        }

        @Override
        public String toString() {
            return "{\"$or\":" + conditions + '}';
        }

        @Override
        public <T> Query<T> buildQuery(Query<T> query) throws Exception {
            //fixme
            var op = conditions.get(0);
            var length = conditions.size();
            query.append('(');
            op.buildQuery(query);
            for (int i = 1; i < length; i++) {
                query.append(" OR ");
                conditions.get(i).buildQuery(query).append(')');
            }
            return query;
        }

        @Override
        public JsonNode toJson(ObjectMapper mapper) {
            var node = mapper.createObjectNode();
            var array = mapper.createArrayNode();
            array.addPOJO(conditions);
            node.set("$or", array);
            return node;
        }
    }
}
