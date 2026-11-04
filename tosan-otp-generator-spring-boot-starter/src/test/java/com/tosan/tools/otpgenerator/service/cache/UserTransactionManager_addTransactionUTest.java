package com.tosan.tools.otpgenerator.service.cache;

import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
class UserTransactionManager_addTransactionUTest extends AbstractUserTransactionManagerUTest {

    @Test
    void validData_addsTransactionToBothCaches() {

        var transactionData = sampleTransactionData();

        when(transactionCacheService.getTransaction(any())).thenReturn(null);

        userTransactionManager.addTransaction(USER_ID, transactionData, 30L);

        verify(transactionCacheService).addTransaction(eq(transactionData), any(String.class), eq(30L));
        verify(userTransactionCacheService).addTransactionToUser(eq(USER_ID), any(String.class), eq(30L));
    }

    @Test
    void nullOtpExpiration_usesConfiguredDefaultTtl() {

        var transactionData = sampleTransactionData();

        when(transactionCacheService.getTransaction(any())).thenReturn(null);

        userTransactionManager.addTransaction(USER_ID, transactionData, null);

        verify(transactionCacheService).addTransaction(eq(transactionData), any(String.class), eq(30L));
        verify(userTransactionCacheService).addTransactionToUser(eq(USER_ID), any(String.class), eq(30L));
    }

    @Test
    void otpExpirationWithValue_usesRequestTtl() {

        var transactionData = sampleTransactionData();

        when(transactionCacheService.getTransaction(any())).thenReturn(null);

        userTransactionManager.addTransaction(USER_ID, transactionData, 120L);

        verify(transactionCacheService).addTransaction(eq(transactionData), any(String.class), eq(120L));
        verify(userTransactionCacheService).addTransactionToUser(eq(USER_ID), any(String.class), eq(120L));
    }
}
