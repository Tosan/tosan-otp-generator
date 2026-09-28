package com.tosan.otpgenerator.otpgenerator.service.cache;

import com.tosan.otpgenerator.exception.OtpException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
class OtpConsumptionMarkerService_ensureNotAlreadyConsumedUTest extends AbstractOtpConsumptionMarkerServiceUTest {

    @Test
    void alreadyConsumedTransaction_throwsOtpExceptionWithAlreadyUsedMessage() {

        when(cacheManager.getItemFromCache("OTP_CONSUMED", consumptionCacheKey())).thenReturn(1L);

        OtpException exception = assertThrows(
                OtpException.class,
                () -> otpConsumptionMarker.checkOtpConsumption(USER_ID, TRANSACTION_ID, 1L)
        );

        assertEquals("OTP already used", exception.getMessage());
    }
}
