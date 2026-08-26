package com.tosan.otpgenerator.otpgenerator.service.cache;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
class UserTransactionManager_unlinkTransactionFromUserUTest extends AbstractUserTransactionManagerUTest {

    @Test
    void existingTransaction_removesFromBothCaches() {

        String expectedKey = transactionCacheKey(TRANSACTION_ID, USER_ID);

        userTransactionManager.unlinkTransactionFromUser(USER_ID, TRANSACTION_ID);

        verify(transactionCacheService).removeTransaction(expectedKey);
        verify(userTransactionCacheService).removeTransaction(USER_ID, expectedKey);
    }

    @Test
    void nonExistentTransaction_completesWithoutException() {

        assertDoesNotThrow(() -> userTransactionManager.unlinkTransactionFromUser(USER_ID, "missing-tx"));
    }
}
