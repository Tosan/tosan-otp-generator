package com.tosan.otpgenerator.otpgenerator.service.cache;

import com.tosan.otpgenerator.exception.OtpException;
import com.tosan.otpgenerator.model.OtpData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
class UserTransactionManager_reissueAfterConsumeUTest extends AbstractUserTransactionManagerUTest {

    private OtpData storedTransaction(long issuanceSequence) {

        OtpData transactionData = sampleTransactionData();
        transactionData.setOtpMapData(
                java.util.Map.of(
                        "dataIdentifier", TRANSACTION_ID,
                        "accountNumber", "acc-123",
                        "amount", new java.math.BigDecimal("100.00"),
                        "currency", "USD"));
        transactionData.setOtpLength(6);
        transactionData.setIssuanceSequence(issuanceSequence);
        return transactionData;
    }

    private OtpData reissueRequest(OtpData stored) {

        OtpData reissued = sampleTransactionData();
        reissued.setOtpMapData(stored.getOtpMapData());
        reissued.setOtpLength(6);
        return reissued;
    }

    @Test
    void consumedTransaction_reissueAssignsNextSequenceAndReplacesEntry() {

        OtpData stored = storedTransaction(1L);
        String cacheKey = transactionCacheKey(
                realOtpUtil.buildTransactionDataString(stored), USER_ID);

        when(transactionCacheService.getTransaction(cacheKey)).thenReturn(stored);
        when(consumptionMarker.getConsumedCount(eq(USER_ID), any())).thenReturn(1L);

        OtpData reissued = reissueRequest(stored);

        long sequence = userTransactionManager.addTransaction(USER_ID, reissued);

        assertEquals(2L, sequence);
        assertEquals(2L, reissued.getIssuanceSequence());
        verify(transactionCacheService).replaceTransaction(eq(reissued), eq(cacheKey), eq(30L));
    }

    @Test
    void activeTransaction_reissueRejected() {

        OtpData stored = storedTransaction(1L);
        String cacheKey = transactionCacheKey(
                realOtpUtil.buildTransactionDataString(stored), USER_ID);

        when(transactionCacheService.getTransaction(cacheKey)).thenReturn(stored);
        when(consumptionMarker.getConsumedCount(eq(USER_ID), any())).thenReturn(0L);

        OtpData reissued = reissueRequest(stored);

        OtpException exception = assertThrows(OtpException.class,
                () -> userTransactionManager.addTransaction(USER_ID, reissued));

        assertEquals("Generated Otp is not expired yet!", exception.getMessage());
        verify(transactionCacheService, never()).replaceTransaction(any(), any(), eq(30L));
    }

    @Test
    void expiredTransactionButConsumedMarkerAlive_reissueGetsFreshSequence() {

        String cacheKey = transactionCacheKey(
                realOtpUtil.buildTransactionDataString(storedTransaction(1L)), USER_ID);

        when(transactionCacheService.getTransaction(cacheKey)).thenReturn(null);
        when(consumptionMarker.getConsumedCount(eq(USER_ID), any())).thenReturn(1L);

        OtpData reissued = storedTransaction(1L);
        reissued.setIssuanceSequence(null);

        long sequence = userTransactionManager.addTransaction(USER_ID, reissued);

        assertEquals(2L, sequence);
        verify(transactionCacheService).addTransaction(eq(reissued), eq(cacheKey), eq(30L));
    }
}
