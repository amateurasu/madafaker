package com.viettel.utils.condition.logic;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.utils.condition.Query;
import com.viettel.utils.condition.reflection.ObjectReflect;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@SuppressWarnings("unchecked")
public abstract class ComparableCondition<C> extends ObjectCondition {

    protected final Comparable<C> value;

    public ComparableCondition(String fieldExp, Comparable<C> value) {
        super(fieldExp);
        this.value = value;
    }

    public static <C> ComparableCondition<C> of(String exp, String op, Comparable<C> value) throws JsonParseException {
        switch (op) {
            case "eq": return new EQ<>(exp, value);
            case "gt": return new GT<>(exp, value);
            case "ge": return new GE<>(exp, value);
            case "lt": return new LT<>(exp, value);
            case "le": return new LE<>(exp, value);
            case "neq": return new NEQ<>(exp, value);
            default:
                throw new JsonParseException(null, "Expect numeric comparison expression but got '" + op + "'");
        }
    }

    public static class EQ<C> extends ComparableCondition<C> {
        public EQ(String field, Comparable<C> value) {
            super(field, value);
        }

        @Override
        public <T> boolean evaluate(ObjectReflect<T> reflect) throws Exception {
            var value = (C) reflect.getValue(expression);
            return this.value.compareTo(value) == 0;
        }

        @Override
        public <T> Query<T> buildQuery(Query<T> query) throws Exception {
            return query.field(expression).append(" = ?").addParam(value);
        }

        @Override
        public JsonNode toJson(ObjectMapper mapper) {
            var node = mapper.createArrayNode();
            node.add(expression);
            node.add("eq");
            node.addPOJO(value.toString());
            return node;
        }
    }

    public static class NEQ<C> extends ComparableCondition<C> {
        public NEQ(String field, Comparable<C> value) {
            super(field, value);
        }

        @Override
        public <T> boolean evaluate(ObjectReflect<T> reflect) throws Exception {
            var value = (C) reflect.getValue(expression);
            return this.value.compareTo(value) != 0;
        }

        @Override
        public <T> Query<T> buildQuery(Query<T> query) throws Exception {
            return query.field(expression).append(" = ?").addParam(value);
        }

        @Override
        public JsonNode toJson(ObjectMapper mapper) {
            var node = mapper.createArrayNode();
            node.add(expression);
            node.add("neq");
            node.addPOJO(value.toString());
            return node;
        }
    }

    public static class GT<C> extends ComparableCondition<C> {
        public GT(String field, Comparable<C> value) {
            super(field, value);
        }

        @Override
        public <T> boolean evaluate(ObjectReflect<T> reflect) throws Exception {
            var value = (C) reflect.getValue(expression);
            return this.value.compareTo(value) < 0;
        }

        @Override
        public <T> Query<T> buildQuery(Query<T> query) throws Exception {
            return query.field(expression).append(" > ?").addParam(value);
        }

        @Override
        public JsonNode toJson(ObjectMapper mapper) {
            var node = mapper.createArrayNode();
            node.add(expression);
            node.add("gt");
            node.addPOJO(value.toString());
            return node;
        }
    }

    public static class GE<C> extends ComparableCondition<C> {
        public GE(String field, Comparable<C> value) {
            super(field, value);
        }

        @Override
        public <T> boolean evaluate(ObjectReflect<T> reflect) throws Exception {
            var value = (C) reflect.getValue(expression);
            return this.value.compareTo(value) <= 0;
        }

        @Override
        public <T> Query<T> buildQuery(Query<T> query) throws Exception {
            return query.field(expression).append(" >= ?").addParam(value);
        }

        @Override
        public JsonNode toJson(ObjectMapper mapper) {
            var node = mapper.createArrayNode();
            node.add(expression);
            node.add("ge");
            node.addPOJO(value.toString());
            return node;
        }
    }

    public static class LT<C> extends ComparableCondition<C> {
        public LT(String field, Comparable<C> value) {
            super(field, value);
        }

        @Override
        public <T> boolean evaluate(ObjectReflect<T> reflect) throws Exception {
            var value = (C) reflect.getValue(expression);
            return this.value.compareTo(value) > 0;
        }

        @Override
        public <T> Query<T> buildQuery(Query<T> query) throws Exception {
            return query.field(expression).append(" < ?").addParam(value);
        }

        @Override
        public JsonNode toJson(ObjectMapper mapper) {
            var node = mapper.createArrayNode();
            node.add(expression);
            node.add("lt");
            node.addPOJO(value.toString());
            return node;
        }
    }

    /** "Less than or equal" condition class */
    public static class LE<C> extends ComparableCondition<C> {
        public LE(String field, Comparable<C> value) {
            super(field, value);
        }

        @Override
        public <T> boolean evaluate(ObjectReflect<T> reflect) throws Exception {
            var value = (C) reflect.getValue(expression);
            return this.value.compareTo(value) >= 0;
        }

        @Override
        public <T> Query<T> buildQuery(Query<T> query) throws Exception {
            return query.field(expression).append(" <= ?").addParam(value);
        }

        @Override
        public JsonNode toJson(ObjectMapper mapper) {
            var node = mapper.createArrayNode();
            node.add(expression);
            node.add("le");
            node.addPOJO(value.toString());
            return node;
        }
    }
}
