package com.viettel.ems.model;

import com.viettel.utils.condition.ICondition;
import lombok.Data;

@Data
public class Filter {
    private final ICondition condition;

    private final int pageSize;
    private final int page;
}
