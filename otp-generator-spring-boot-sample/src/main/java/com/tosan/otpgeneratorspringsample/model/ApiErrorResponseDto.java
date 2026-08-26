package com.tosan.otpgeneratorspringsample.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiErrorResponseDto {
    private String message;
    private String errorCode;
    private int status;
    private long timestamp;

    public ApiErrorResponseDto(String message, String errorCode, int status) {
        this.message = message;
        this.errorCode = errorCode;
        this.status = status;
        this.timestamp = System.currentTimeMillis();
    }
}