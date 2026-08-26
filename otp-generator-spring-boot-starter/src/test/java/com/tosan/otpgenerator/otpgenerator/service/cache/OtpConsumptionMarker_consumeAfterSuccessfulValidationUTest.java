package com.tosan.otpgenerator.otpgenerator.service.cache;

import com.tosan.otpgenerator.exception.OtpException;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
class OtpConsumptionMarkerService_consumeAfterSuccessfulValidationUTest extends AbstractOtpConsumptionMarkerServiceUTest {

    @Test
    void incrementsCounterSetsTtlAndUnlinksTransaction() {

        when(cacheManager.incrementAndGetAtomicItem("OTP_CONSUMED", consumptionCacheKey()))
                .thenReturn(1L);

        otpConsumptionMarker.markOtpConsumed(USER_ID, TRANSACTION_ID);

        verify(cacheManager).expireAtomicItem(
                "OTP_CONSUMED", consumptionCacheKey(),
                otpProperties.getTimeStepSeconds(), TimeUnit.SECONDS);
        verify(userTransactionManager).unlinkTransactionFromUser(USER_ID, TRANSACTION_ID);
    }

    @Test
    void counterGreaterThanOneDueToConcurrentRequests_throwsOtpExceptionAndPreventsDoubleConsumption() {

        when(cacheManager.incrementAndGetAtomicItem("OTP_CONSUMED", consumptionCacheKey()))
                .thenReturn(3L);

        OtpException exception = assertThrows(
                OtpException.class,
                () -> otpConsumptionMarker.markOtpConsumed(USER_ID, TRANSACTION_ID)
        );

        assertEquals("OTP already used or expired", exception.getMessage());
    }
}
