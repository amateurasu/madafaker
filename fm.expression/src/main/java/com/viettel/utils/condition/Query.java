package com.viettel.utils.condition;

import com.viettel.utils.condition.reflection.Reflect;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@RequiredArgsConstructor
public class Query<T> {

    private final StringBuilder sql = new StringBuilder();
    private final List<Object> params = new ArrayList<>();

    private final Reflect reflect;

    public Query(Class<?> klass) {
        this(new Reflect(klass));
    }

    public Query<T> select(String... fields) {

        return this;
    }

    public String getSql() {
        return sql.toString();
    }

    public Object[] getParamArray() {
        return params.toArray();
    }

    public Query<T> append(String s) {
        sql.append(s);
        return this;
    }

    public Query<T> append(char c) {
        sql.append(c);
        return this;
    }

    public Query<T> append(Object o) {
        sql.append(o);
        return this;
    }

    public <L> Query<T> prepare(List<L> list) {
        var length = list.size();
        if (length > 0) {
            sql.append("?").append(", ?".repeat(length - 1));
        }
        return this;
    }

    public Query<T> addParam(Object param) {
        params.add(param);
        return this;
    }

    public Query<T> addParams(List<?> params) {
        this.params.addAll(params);
        return this;
    }

    public Query<T> addParams(Object... params) {
        Collections.addAll(this.params, params);
        return this;
    }

    public Query<T> field(String field) throws NoSuchFieldException {
        sql.append(reflect.getDbField(field));
        return this;
    }

    public String getField(String field) throws NoSuchFieldException {
        return reflect.getField(field);
    }
}
