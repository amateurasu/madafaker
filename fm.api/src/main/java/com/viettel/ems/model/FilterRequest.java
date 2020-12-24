package com.viettel.ems.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.viettel.utils.condition.Condition;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FilterRequest extends Pagination {

    @JsonProperty("filters")
    private Condition condition;
}
