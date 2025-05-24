package com.bcaf.finapay.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDto<T> {
    @JsonProperty("status_code")
    private int statusCode;
    private String status;
    private String message;
    private T data;
}
