package com.tosan.otpgenerator.otpgenerator.service.cache;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
class UserTransactionCacheService_getTransactionIdsUTest extends AbstractUserTransactionCacheServiceUTest {

    @Test
    void userWithExistingTransactions_returnsTransactionIds() {

        String transactionKey = transactionCacheKey(TRANSACTION_ID, USER_ID);
        Set<String> expected = Set.of(transactionKey);

        when(cacheManager.getItemFromCache("USER", USER_ID)).thenReturn(expected);

        Set<String> result = userTransactionCacheService.getTransactionIds(USER_ID);

        assertEquals(expected, result);
    }

    @Test
    void userWithNoTransactions_returnsEmptySet() {

        when(cacheManager.getItemFromCache("USER", USER_ID)).thenReturn(null);

        Set<String> result = userTransactionCacheService.getTransactionIds(USER_ID);

        assertTrue(result.isEmpty());
    }
}
