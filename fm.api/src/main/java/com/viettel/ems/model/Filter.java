package com.viettel.ems.model;

import com.viettel.utils.condition.Condition;
import lombok.Data;

@Data
public class Filter {
    private final Condition condition;

    private final int pageSize;
    private final int page;
}
