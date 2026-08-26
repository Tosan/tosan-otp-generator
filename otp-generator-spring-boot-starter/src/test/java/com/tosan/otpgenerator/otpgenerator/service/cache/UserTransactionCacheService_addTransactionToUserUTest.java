package com.tosan.otpgenerator.otpgenerator.service.cache;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
class UserTransactionCacheService_addTransactionToUserUTest extends AbstractUserTransactionCacheServiceUTest {

    @Test
    void validData_addsTransaction() {

        String transactionKey = transactionCacheKey(TRANSACTION_ID, USER_ID);

        userTransactionCacheService.addTransactionToUser(USER_ID, transactionKey, 30L);

        ArgumentCaptor<Set<String>> setCaptor = ArgumentCaptor.forClass(Set.class);
        verify(cacheManager).addItemToCache(
                eq("USER"), eq(USER_ID), setCaptor.capture(), eq(30L), eq(TimeUnit.SECONDS));
        assertTrue(setCaptor.getValue().contains(transactionKey));
    }

    @Test
    void userWithNoExistingTransactions_createsNewSetWithSingleId() {

        String transactionKey = transactionCacheKey(TRANSACTION_ID, USER_ID);
        when(cacheManager.getItemFromCache("USER", USER_ID)).thenReturn(null);

        userTransactionCacheService.addTransactionToUser(USER_ID, transactionKey, 30L);

        ArgumentCaptor<Set<String>> setCaptor = ArgumentCaptor.forClass(Set.class);
        verify(cacheManager).addItemToCache(
                eq("USER"), eq(USER_ID), setCaptor.capture(), eq(30L), eq(TimeUnit.SECONDS));
        assertEquals(Set.of(transactionKey), setCaptor.getValue());
    }

}
