package com.tosan.otpgeneratorspringsample.model;

import com.tosan.otpgenerator.model.OtpVerificationRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DefaultOtpValidationRequestDto implements OtpVerificationRequest {

    private String otp;

    private String userIdentifier;

    private Map<String,Object> otpData;

    private Integer otpLength;

}
