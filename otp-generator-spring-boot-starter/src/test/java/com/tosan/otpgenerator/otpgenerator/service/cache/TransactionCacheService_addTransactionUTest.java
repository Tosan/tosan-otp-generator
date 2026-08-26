package com.tosan.otpgenerator.otpgenerator.service.cache;

import com.tosan.otpgenerator.otpgenerator.TestFixtures;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
class TransactionCacheService_addTransactionUTest extends AbstractTransactionCacheServiceUTest {

    @Test
    void validData_storesTransactionWithTtl() {

        var transactionData = TestFixtures.otpData(
                TRANSACTION_ID, "acc-123", new BigDecimal("100.00"), "USD");
        String cacheKey = transactionCacheKey(TRANSACTION_ID, USER_ID);
        long ttl = otpProperties.getTimeStepSeconds();

        transactionCacheService.addTransaction(transactionData, cacheKey, ttl);

        verify(cacheManager).addItemToCache(
                eq("TRANSACTION"),
                eq(cacheKey),
                eq(transactionData),
                eq(ttl),
                eq(TimeUnit.SECONDS)
        );
    }
}
