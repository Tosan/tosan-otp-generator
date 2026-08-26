package com.tosan.otpgenerator.otpgenerator.service;

import com.tosan.otpgenerator.otpgenerator.TestFixtures;
import com.tosan.otpgenerator.otpgenerator.TestFixtures.TestOtpData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
class OcraOtpProvider_matchesSubmittedOtpUTest extends AbstractOcraOtpProviderUTest {

    private Method matchesSubmittedOtpMethod;

    @BeforeEach
    void setUp() throws Exception {
        matchesSubmittedOtpMethod = getMatchesSubmittedOtpMethod();
    }

    @Test
    void submittedOtpMatchesGeneratedOtp_returnsTrue() throws Exception {

        TestOtpData transactionData = sampleTransactionData();
        byte[] secretKey = TestFixtures.validSecretKey();
        long timeCounter = timeStepUtil.currentTimeStep(otpProperties.getTimeStepSeconds());
        String generatedOtp = invokeGenerateOtp(secretKey, transactionData, timeCounter);

        boolean result = invokeMatchesSubmittedOtp(
                matchesSubmittedOtpMethod, secretKey, transactionData, timeCounter, generatedOtp);

        assertTrue(result);
    }

    @Test
    void submittedOtpMatchesWithDifferentCase_returnsFalse() throws Exception {

        ocraOtpProvider = createOcraOtpProvider(otpUtil);
        TestOtpData transactionData = sampleTransactionData();
        byte[] secretKey = TestFixtures.validSecretKey();
        long timeCounter = timeStepUtil.currentTimeStep(otpProperties.getTimeStepSeconds());

        org.mockito.Mockito.when(otpUtil.buildOtpPayload(org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyLong())).thenReturn("1|dataIdentifier=tx-1");
        org.mockito.Mockito.when(otpUtil.generateHmac(org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any())).thenReturn(new byte[20]);
        org.mockito.Mockito.when(otpUtil.truncate(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.eq(6)))
                .thenReturn("AbC123");

        boolean result = invokeMatchesSubmittedOtp(
                matchesSubmittedOtpMethod, secretKey, transactionData, timeCounter, "abc123");

        assertFalse(result);
    }
}
