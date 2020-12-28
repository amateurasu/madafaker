package com.viettel.utils.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.viettel.utils.condition.Condition;
import lombok.Data;

import java.util.List;

@Data
public class Query<T> {

    @JsonProperty("fields")
    private List<String> fields;

    private Condition condition;


    public Query<T> build() {

        return this;
    }
}
