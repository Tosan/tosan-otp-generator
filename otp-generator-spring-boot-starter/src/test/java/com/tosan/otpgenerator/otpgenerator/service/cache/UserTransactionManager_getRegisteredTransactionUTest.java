package com.tosan.otpgenerator.otpgenerator.service.cache;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
class UserTransactionManager_getRegisteredTransactionUTest extends AbstractUserTransactionManagerUTest {

    @Test
    void existingTransaction_returnsTransactionData() {

        String expectedKey = transactionCacheKey(TRANSACTION_ID, USER_ID);
        var transactionData = sampleTransactionData();

        when(transactionCacheService.getTransaction(expectedKey)).thenReturn(transactionData);

        var result = userTransactionManager.getRegisteredTransaction(USER_ID, TRANSACTION_ID);

        assertEquals(transactionData, result);
    }

    @Test
    void nonExistentOrExpiredTransaction_returnsNull() {

        String expectedKey = transactionCacheKey(TRANSACTION_ID, USER_ID);

        when(transactionCacheService.getTransaction(expectedKey)).thenReturn(null);

        var result = userTransactionManager.getRegisteredTransaction(USER_ID, TRANSACTION_ID);

        assertNull(result);
    }
}
