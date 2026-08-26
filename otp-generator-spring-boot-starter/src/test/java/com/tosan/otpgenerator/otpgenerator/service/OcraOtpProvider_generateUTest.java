package com.tosan.otpgenerator.otpgenerator.service;

import com.tosan.otpgenerator.exception.OtpException;
import com.tosan.otpgenerator.otpgenerator.TestFixtures;
import com.tosan.otpgenerator.otpgenerator.TestFixtures.TestOtpRequest;
import com.tosan.otpgenerator.otpgenerator.TestFixtures.TestOtpData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
class OcraOtpProvider_generateUTest extends AbstractOcraOtpProviderUTest {

    @Test
    void validSecretKeyAndTransactionData_returnsOtpAndStoresTransaction() {

        TestOtpData transactionData = sampleTransactionData();
        TestOtpRequest request = TestFixtures.otpRequest(USER_ID, transactionData);

        when(secretKeyProvider.getSecretKey(USER_ID)).thenReturn(TestFixtures.validSecretKey());

        String otp = ocraOtpProvider.generate(request);

        assertNotNull(otp);
        assertFalse(otp.isBlank());
        verify(userTransactionManager).addTransaction(eq(USER_ID), any());
    }


    @Test
    void secretKeyProviderThrowsException_throwsOtpExceptionWithFailedToGenerateMessage() {

        TestOtpData transactionData = sampleTransactionData();
        TestOtpRequest request = TestFixtures.otpRequest(USER_ID, transactionData);

        when(secretKeyProvider.getSecretKey(USER_ID)).thenThrow(new RuntimeException("secret key failure"));

        OtpException exception = assertThrows(OtpException.class, () -> ocraOtpProvider.generate(request));

        assertEquals("Failed to generate OTP", exception.getMessage());
    }

    @Test
    void hmacGenerationFailsDueToInvalidAlgorithm_throwsOtpExceptionWithHmacFailureMessage() {

        otpProperties.setCryptoAlgorithm("InvalidAlgorithm");
        ocraOtpProvider = createOcraOtpProviderWithRealUtil();

        TestOtpData transactionData = sampleTransactionData();
        TestOtpRequest request = TestFixtures.otpRequest(USER_ID, transactionData);

        when(secretKeyProvider.getSecretKey(USER_ID)).thenReturn(TestFixtures.validSecretKey());

        OtpException exception = assertThrows(OtpException.class, () -> ocraOtpProvider.generate(request));

        assertEquals("Failed to generate OTP", exception.getMessage());
        assertEquals("Failed to compute OTP using configured HMAC algorithm", exception.getCause().getMessage());
    }

    @Test
    void generatedOtpIsNullOrBlank_throwsOtpExceptionWithGeneratedOtpIsNullMessage() {

        ocraOtpProvider = createOcraOtpProvider(otpUtil);

        TestOtpData transactionData = sampleTransactionData();
        TestOtpRequest request = TestFixtures.otpRequest(USER_ID, transactionData);

        when(secretKeyProvider.getSecretKey(USER_ID)).thenReturn(TestFixtures.validSecretKey());
        when(otpUtil.buildTransactionDataString(any())).thenReturn("dataIdentifier=tx-1");
        when(otpUtil.buildOtpPayload(anyString(), anyLong())).thenReturn("1|dataIdentifier=tx-1");
        when(otpUtil.generateHmac(anyString(), any(), any())).thenReturn(new byte[20]);
        when(otpUtil.truncate(any(), eq(6))).thenReturn("   ");

        OtpException exception = assertThrows(OtpException.class, () -> ocraOtpProvider.generate(request));

        assertEquals("Failed to generate OTP", exception.getMessage());
        assertEquals("Generated OTP is null ", exception.getCause().getMessage());
    }
}
