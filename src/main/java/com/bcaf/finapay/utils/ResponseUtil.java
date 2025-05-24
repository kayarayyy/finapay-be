package com.bcaf.finapay.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.bcaf.finapay.dto.ResponseDto;

public class ResponseUtil {

    public static <T> ResponseEntity<ResponseDto> success(T data, String message) {
        return ResponseEntity.ok(new ResponseDto<>(200, "success", message, data));
    }

    public static <T> ResponseEntity<ResponseDto> created(T data, String message) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDto<>(201,"success", message, data));
    }
}
