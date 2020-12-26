package com.viettel.utils.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.viettel.utils.condition.Condition;
import lombok.Data;

import java.util.List;

@Data
public class Query {

    @JsonProperty("fields")
    private List<String> fields;

    private Condition filter;


    public Query build() {

        return this;
    }
}
