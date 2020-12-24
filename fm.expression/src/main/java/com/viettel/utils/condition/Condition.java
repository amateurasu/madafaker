package com.viettel.utils.condition;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.viettel.utils.condition.reflection.ObjectReflect;
import com.viettel.utils.condition.reflection.Reflect;

@JsonDeserialize(using = ConditionDeserializer.class)
public interface Condition {

    <T> boolean evaluate(ObjectReflect<T> reflect) throws Exception;

    <T> Query<T> buildQuery(Query<T> query) throws Exception;

    boolean validate(Reflect reflect);

    JsonNode toJson(ObjectMapper mapper);

    default boolean validate(Class<?> klass) {
        return validate(new Reflect(klass));
    }

    default <T> boolean evaluate(T o) throws Exception {
        return evaluate(new ObjectReflect<>(o));
    }

    default <T> Query<T> buildQuery(Class<T> klass) throws Exception {
        return buildQuery(klass, null);
    }

    default <T> Query<T> buildQuery(Class<T> klass, String predicate) throws Exception {
        var table = klass.getAnnotation(Table.class).value();
        var query = new Query<T>(new Reflect(klass)).append("SELECT * FROM " + table + " WHERE ")
            .append(predicate == null || predicate.isBlank() ? "" : predicate + " AND ");
        return buildQuery(query);
    }
}
