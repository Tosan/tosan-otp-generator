package com.tosan.otpgenerator.otpgenerator.service.cache;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
class UserTransactionCacheService_removeTransactionUTest extends AbstractUserTransactionCacheServiceUTest {

    @Test
    void existingTransactionId_updatesSet() {

        String transactionKey = transactionCacheKey(TRANSACTION_ID, USER_ID);
        String otherKey = transactionCacheKey("tx-2", USER_ID);
        Set<String> existing = new HashSet<>(Set.of(transactionKey, otherKey));
        when(cacheManager.getItemFromCache("USER", USER_ID)).thenReturn(existing);

        userTransactionCacheService.removeTransaction(USER_ID, transactionKey);

        ArgumentCaptor<Set> setCaptor = ArgumentCaptor.forClass(Set.class);
        verify(cacheManager).replaceCacheItem(eq("USER"), eq(USER_ID), setCaptor.capture());
        assertFalse(setCaptor.getValue().contains(transactionKey));
        assertEquals(Set.of(otherKey), setCaptor.getValue());
    }

    @Test
    void userHasOnlyOneTransaction_removesUserEntryFromCache() {

        String transactionKey = transactionCacheKey(TRANSACTION_ID, USER_ID);
        when(cacheManager.getItemFromCache("USER", USER_ID)).thenReturn(new HashSet<>(Set.of(transactionKey)));

        userTransactionCacheService.removeTransaction(USER_ID, transactionKey);

        verify(cacheManager).removeItemFromCache("USER", USER_ID);
    }

    @Test
    void userWithNoTransactions_completesWithoutException() {

        when(cacheManager.getItemFromCache("USER", USER_ID)).thenReturn(null);

        assertDoesNotThrow(() -> userTransactionCacheService.removeTransaction(USER_ID, "missing-key"));
        verify(cacheManager, never()).replaceCacheItem(eq("USER"), eq(USER_ID), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void nonExistentTransactionId_leavesSetUnchangedWithoutException() {

        String transactionKey = transactionCacheKey(TRANSACTION_ID, USER_ID);
        Set<String> existing = new HashSet<>(Set.of(transactionKey));
        when(cacheManager.getItemFromCache("USER", USER_ID)).thenReturn(existing);

        assertDoesNotThrow(() -> userTransactionCacheService.removeTransaction(USER_ID, "missing-key"));

        ArgumentCaptor<Set> setCaptor = ArgumentCaptor.forClass(Set.class);
        verify(cacheManager).replaceCacheItem(eq("USER"), eq(USER_ID), setCaptor.capture());
        assertEquals(Set.of(transactionKey), setCaptor.getValue());
    }
}
