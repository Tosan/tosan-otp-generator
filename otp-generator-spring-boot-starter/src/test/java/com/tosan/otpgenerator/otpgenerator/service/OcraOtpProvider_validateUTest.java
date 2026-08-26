package com.tosan.otpgenerator.otpgenerator.service;

import com.tosan.otpgenerator.exception.OtpException;
import com.tosan.otpgenerator.otpgenerator.TestFixtures;
import com.tosan.otpgenerator.otpgenerator.TestFixtures.TestOtpRequest;
import com.tosan.otpgenerator.otpgenerator.TestFixtures.TestOtpVerificationRequest;
import com.tosan.otpgenerator.otpgenerator.TestFixtures.TestOtpData;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
class OcraOtpProvider_validateUTest extends AbstractOcraOtpProviderUTest {

    @Test
    void validOtp_succeedsAndMarksOtpAsConsumed() {

        TestOtpData transactionData = sampleTransactionData();
        TestOtpRequest generateRequest = TestFixtures.otpRequest(USER_ID, transactionData);

        when(secretKeyProvider.getSecretKey(USER_ID)).thenReturn(TestFixtures.validSecretKey());
        when(userTransactionManager.getRegisteredTransaction(eq(USER_ID), any()))
                .thenReturn(null)
                .thenReturn(transactionData);

        String generatedOtp = ocraOtpProvider.generate(generateRequest);

        TestOtpVerificationRequest verificationRequest =
                TestFixtures.otpVerificationRequest(USER_ID, transactionData, generatedOtp);

        assertDoesNotThrow(() -> ocraOtpProvider.validate(verificationRequest));

        InOrder inOrder = inOrder(consumptionMarker);
        inOrder.verify(consumptionMarker).checkOtpConsumption(eq(USER_ID), any());
        inOrder.verify(consumptionMarker).markOtpConsumed(eq(USER_ID), any());
    }

    @Test
    void transactionNotFoundInCache_throwsOtpExceptionWithTransactionExpiredMessage() {

        TestOtpData transactionData = sampleTransactionData();
        TestOtpRequest generateRequest = TestFixtures.otpRequest(USER_ID, transactionData);

        when(secretKeyProvider.getSecretKey(USER_ID)).thenReturn(TestFixtures.validSecretKey());
        String generatedOtp = ocraOtpProvider.generate(generateRequest);

        TestOtpVerificationRequest request =
                TestFixtures.otpVerificationRequest(USER_ID, transactionData, generatedOtp);
        when(userTransactionManager.getRegisteredTransaction(eq(USER_ID), any())).thenReturn(null);

        OtpException exception = assertThrows(OtpException.class, () -> ocraOtpProvider.validate(request));

        assertEquals("Transaction expired or not found", exception.getMessage());
    }

    @Test
    void otpAlreadyConsumed_throwsOtpExceptionWithAlreadyUsedMessage() {

        TestOtpData transactionData = sampleTransactionData();
        TestOtpVerificationRequest request =
                TestFixtures.otpVerificationRequest(USER_ID, transactionData, "123456");

        doThrow(new OtpException("OTP already used or expired"))
                .when(consumptionMarker).checkOtpConsumption(eq(USER_ID), any());

        OtpException exception = assertThrows(OtpException.class, () -> ocraOtpProvider.validate(request));

        assertEquals("OTP already used or expired", exception.getMessage());
    }

    @Test
    void incorrectOtpAcrossAllTimeWindows_throwsOtpExceptionWithInvalidOtpMessage() {

        TestOtpData transactionData = sampleTransactionData();
        TestOtpVerificationRequest request =
                TestFixtures.otpVerificationRequest(USER_ID, transactionData, "000000");

        when(secretKeyProvider.getSecretKey(USER_ID)).thenReturn(TestFixtures.validSecretKey());

        OtpException exception = assertThrows(OtpException.class, () -> ocraOtpProvider.validate(request));

        assertEquals("Invalid OTP", exception.getMessage());
    }

    @Test
    void secretKeyProviderFails_throwsOtpExceptionWithInvalidOtpMessage() {

        TestOtpData transactionData = sampleTransactionData();
        TestOtpVerificationRequest request =
                TestFixtures.otpVerificationRequest(USER_ID, transactionData, "123456");

        when(secretKeyProvider.getSecretKey(USER_ID)).thenThrow(new RuntimeException("secret key failure"));

        OtpException exception = assertThrows(OtpException.class, () -> ocraOtpProvider.validate(request));

        assertEquals("Invalid OTP", exception.getMessage());
    }
}
