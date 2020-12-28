package com.viettel.ems.model.cm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ResponseData {

    @JsonProperty("ne_ip")
    private String neIp;

    @JsonProperty("cmStatus")
    private String cmStatus;

    @JsonProperty("extra")
    private List<Extra> extra;
}
