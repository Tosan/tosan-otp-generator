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

        otpConsumptionMarker.markOtpConsumed(USER_ID, TRANSACTION_ID, 1L);

        long expectedTtl = otpProperties.getTimeStepSeconds() * (1 + otpProperties.getAllowedClockSkew());
        verify(cacheManager).expireAtomicItem(
                "OTP_CONSUMED", consumptionCacheKey(),
                expectedTtl, TimeUnit.SECONDS);
    }

    @Test
    void counterGreaterThanOneDueToConcurrentRequests_throwsOtpExceptionAndPreventsDoubleConsumption() {

        when(cacheManager.incrementAndGetAtomicItem("OTP_CONSUMED", consumptionCacheKey()))
                .thenReturn(3L);

        OtpException exception = assertThrows(
                OtpException.class,
                () -> otpConsumptionMarker.markOtpConsumed(USER_ID, TRANSACTION_ID, 1L)
        );

        assertEquals("OTP already used or expired", exception.getMessage());
    }
}
