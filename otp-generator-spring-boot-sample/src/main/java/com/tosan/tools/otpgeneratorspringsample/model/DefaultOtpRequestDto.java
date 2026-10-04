package com.tosan.tools.otpgeneratorspringsample.model;

import com.tosan.tools.otpgenerator.model.OtpRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DefaultOtpRequestDto implements OtpRequest {

    private String userIdentifier;

    private Map<String,Object> otpData;

    private Integer otpLength;

}
