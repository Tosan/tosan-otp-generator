package com.tosan.otpgenerator.utils;

import com.tosan.otpgenerator.model.OtpRequest;
import com.tosan.otpgenerator.model.OtpVerificationRequest;
import io.micrometer.common.util.StringUtils;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
public class OtpValidator {

    public void validateGenerate(OtpRequest request) {

        checkRequest(request);

        validateCommon(request.getUserIdentifier());
    }

    private static void checkRequest(OtpRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Invalid request for OTP generation");
        }
    }

    public void validateOtpVerification(OtpVerificationRequest request) {

       checkRequest(request);

        validateCommon(request.getUserIdentifier());

        if (StringUtils.isEmpty(request.getOtp())) {
            throw new IllegalArgumentException("otp must not be null or empty");
        }
    }


    private void validateCommon(String userId) {
        if (StringUtils.isEmpty(userId)) {
            throw new IllegalArgumentException("userId must not be null or empty");
        }
    }
}