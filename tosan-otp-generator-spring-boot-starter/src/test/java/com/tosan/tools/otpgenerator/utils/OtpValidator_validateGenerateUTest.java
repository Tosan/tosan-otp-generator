package com.tosan.tools.otpgenerator.utils;

import com.tosan.tools.otpgenerator.TestFixtures;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class OtpValidator_validateGenerateUTest extends AbstractOtpValidatorUTest {

    @Test
    void allValidFields_passesWithoutException() {

        var transactionData = sampleTransactionData();
        var request = TestFixtures.otpRequest(USER_ID, transactionData);

        assertDoesNotThrow(() -> otpValidator.validateGenerate(request));
    }
}
