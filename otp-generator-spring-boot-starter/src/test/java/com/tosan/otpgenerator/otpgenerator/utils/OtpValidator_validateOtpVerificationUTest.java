package com.tosan.otpgenerator.otpgenerator.utils;

import com.tosan.otpgenerator.otpgenerator.TestFixtures;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class OtpValidator_validateOtpVerificationUTest extends AbstractOtpValidatorUTest {

    @Test
    void allValidFieldsIncludingOtp_passesWithoutException() {

        var transactionData = sampleTransactionData();
        var request = TestFixtures.otpVerificationRequest(USER_ID, transactionData, "123456");

        assertDoesNotThrow(() -> otpValidator.validateOtpVerification(request));
    }
}
