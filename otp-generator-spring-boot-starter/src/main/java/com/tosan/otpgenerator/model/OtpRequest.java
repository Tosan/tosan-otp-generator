package com.tosan.otpgenerator.model;


import java.util.Map;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
public interface OtpRequest {

    /**
     * User identifier.
     */
    String getUserIdentifier();

    /**
     * Transaction data for OTP generation.
     */
    Map<String,Object> getOtpData();

    /**
     * OTP length (number of digits).
     */
    Integer getOtpLength();
}
