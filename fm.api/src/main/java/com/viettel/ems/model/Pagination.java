package com.viettel.ems.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Pagination {

    @JsonProperty("page")
    private int page;

    @JsonProperty("page_size")
    private int pageSize;
}
