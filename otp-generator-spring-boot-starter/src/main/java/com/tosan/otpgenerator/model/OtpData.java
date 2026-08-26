package com.tosan.otpgenerator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OtpData {

    /**
     * Map of transaction data used in OTP generation and validation.
     */
    private Map<String, Object> otpMapData;

    /**
     * OTP length (number of digits).
     */
    private Integer otpLength;
}
