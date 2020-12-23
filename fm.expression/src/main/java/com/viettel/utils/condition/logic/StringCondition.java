package com.viettel.utils.condition.logic;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.utils.condition.Query;
import com.viettel.utils.condition.logic.string.WordIterable;
import com.viettel.utils.condition.reflection.ObjectReflect;
import lombok.Getter;
import lombok.Setter;

import java.util.Iterator;

@Getter
@Setter
public abstract class StringCondition extends ObjectCondition {

    protected String value;

    public StringCondition(String fieldExp, String value) {
        super(fieldExp);
        this.value = value;
    }

    public static StringCondition of(String fields, String op, String value) throws JsonParseException {
        switch (op) {
            case "alike": return new ALIKE(fields, value);
            case "match": return new MATCH(fields, value);
            default:
                throw new JsonParseException(null, "Expect string comparison expression but got '" + op + '\'');
        }
    }

    public static class CONTAINS extends StringCondition {
        public CONTAINS(String fields, String value) {
            super(fields, value);
        }

        @Override
        public <T> boolean evaluate(ObjectReflect<T> reflect) throws Exception {
            var field = reflect.getValue(expression).toString();
            return match(field, value);
        }

        private static boolean match(String s1, String s2) {
            for (String s : new WordIterable(s1)) {
                if (s2.contains(s)) return true;
            }

            return false;
        }

        @Override
        public <T> Query<T> buildQuery(Query<T> query) throws Exception {
            return query.append("INSTR(").append(expression).append(", ?) > 0").addParam(value);
        }

        @Override
        public JsonNode toJson(ObjectMapper mapper) {
            var array = mapper.createArrayNode();
            array.add(expression);
            array.add("match");
            array.add(value);
            return array;
        }
    }

    public static class MATCH extends StringCondition {
        public MATCH(String fields, String value) {
            super(fields, value);
        }

        @Override
        public <T> boolean evaluate(ObjectReflect<T> reflect) throws Exception {
            var field = reflect.getValue(expression).toString();
            return match(field, value);
        }

        private static boolean match(String s1, String s2) {
            for (String s : new WordIterable(s1)) {
                if (s2.contains(s)) return true;
            }

            return false;
        }

        @Override
        public <T> Query<T> buildQuery(Query<T> query) throws Exception {
            return query.append("MATCH (").append(expression).append(") AGAINST (? IN BOOLEAN MODE)").addParam(value);
        }

        @Override
        public JsonNode toJson(ObjectMapper mapper) {
            var array = mapper.createArrayNode();
            array.add(expression);
            array.add("match");
            array.add(value);
            return array;
        }
    }

    public static class ALIKE extends StringCondition {
        public ALIKE(String fields, String value) {
            super(fields, value);
        }

        @Override
        public <T> boolean evaluate(ObjectReflect<T> reflect) throws Exception {
            var field = reflect.getValue(expression).toString();
            return match(field, value);
        }

        private static boolean match(String s1, String s2) {
            for (Iterator<String> iterator = new WordIterable(s1).iterator(); iterator.hasNext(); ) {
                String s = iterator.next();
                if (s2.contains(s)) return true;
            }

            return false;
        }

        @Override
        public <T> Query<T> buildQuery(Query<T> query) throws Exception {
            return query.append("MATCH (")
                .append(expression)
                .append(") AGAINST (? IN NATURAL LANGUAGE MODE)")
                .addParam(value);
        }

        @Override
        public JsonNode toJson(ObjectMapper mapper) {
            var array = mapper.createArrayNode();
            array.add(expression);
            array.add("alike");
            array.add(value);
            return array;
        }
    }
}
