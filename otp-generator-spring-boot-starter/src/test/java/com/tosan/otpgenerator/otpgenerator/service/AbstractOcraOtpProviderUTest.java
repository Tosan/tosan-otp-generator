package com.tosan.otpgenerator.otpgenerator.service;

import com.tosan.otpgenerator.otpgenerator.AbstractUTest;
import com.tosan.otpgenerator.otpgenerator.TestFixtures;
import com.tosan.otpgenerator.otpgenerator.TestFixtures.TestOtpData;
import com.tosan.otpgenerator.service.OcraOtpProvider;
import com.tosan.otpgenerator.service.SecretKeyProvider;
import com.tosan.otpgenerator.service.cache.OtpConsumptionMarkerService;
import com.tosan.otpgenerator.service.cache.UserTransactionManager;
import com.tosan.otpgenerator.utils.OtpUtil;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

import java.lang.reflect.Method;
import java.math.BigDecimal;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
public abstract class AbstractOcraOtpProviderUTest extends AbstractUTest {

    @Mock
    protected SecretKeyProvider secretKeyProvider;

    @Mock
    protected UserTransactionManager userTransactionManager;

    @Mock
    protected OtpConsumptionMarkerService consumptionMarker;

    @Mock
    protected OtpUtil otpUtil;

    protected OcraOtpProvider ocraOtpProvider;

    @BeforeEach
    void ocraOtpProviderSetUp() {
        ocraOtpProvider = createOcraOtpProviderWithRealUtil();
    }

    protected OcraOtpProvider createOcraOtpProvider(OtpUtil util) {
        return new OcraOtpProvider(
                otpProperties,
                userTransactionManager,
                util,
                secretKeyProvider,
                otpValidator,
                timeStepUtil,
                consumptionMarker
        );
    }

    protected OcraOtpProvider createOcraOtpProviderWithRealUtil() {
        return createOcraOtpProvider(realOtpUtil);
    }

    protected TestOtpData sampleTransactionData() {
        return TestFixtures.otpData(
                TRANSACTION_ID, "acc-123", new BigDecimal("100.00"), "USD");
    }

    protected String invokeGenerateOtp(byte[] secretKey, TestOtpData transactionData, long timeCounter)
            throws Exception {
        Method generateOtpMethod = OcraOtpProvider.class.getDeclaredMethod(
                "generateOtp", byte[].class, String.class, long.class, int.class);
        generateOtpMethod.setAccessible(true);
        String transactionDataString = realOtpUtil.buildTransactionDataString(transactionData);
        return (String) generateOtpMethod.invoke(
                ocraOtpProvider, secretKey, transactionDataString, timeCounter, 6);
    }

    protected Method getMatchesSubmittedOtpMethod() throws NoSuchMethodException {
        Method method = OcraOtpProvider.class.getDeclaredMethod(
                "matchesSubmittedOtp", byte[].class, String.class, long.class, String.class, int.class);
        method.setAccessible(true);
        return method;
    }

    protected boolean invokeMatchesSubmittedOtp(
            Method matchesSubmittedOtpMethod,
            byte[] secretKey,
            TestOtpData transactionData,
            long timeCounter,
            String submittedOtp) throws Exception {
        String transactionDataString = realOtpUtil.buildTransactionDataString(transactionData);
        return (boolean) matchesSubmittedOtpMethod.invoke(
                ocraOtpProvider, secretKey, transactionDataString, timeCounter, submittedOtp, 6);
    }
}
