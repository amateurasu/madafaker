package com.viettel.ems.model.cm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Extra {

    @JsonProperty("command")
    private String command;

    @JsonProperty("result")
    private String result;
}
