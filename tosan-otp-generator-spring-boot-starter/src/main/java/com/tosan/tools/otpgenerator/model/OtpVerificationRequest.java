package com.tosan.tools.otpgenerator.model;


/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
public interface OtpVerificationRequest extends OtpRequest {

    /**
     * OTP code submitted by the user.
     */
    String getOtp();
}
