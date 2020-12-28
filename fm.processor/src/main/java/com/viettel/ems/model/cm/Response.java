package com.viettel.ems.model.cm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Response {

    @JsonProperty("response")
    private String response;

    @JsonProperty("response_code")
    private String responseCode;

    @JsonProperty("response_data")
    private List<ResponseData> responseData;
}
