package com.tosan.otpgenerator.otpgenerator.service.cache;

import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
class UserTransactionManager_addTransactionUTest extends AbstractUserTransactionManagerUTest {

    @Test
    void validData_addsTransactionToBothCaches() {

        var transactionData = sampleTransactionData();

        userTransactionManager.addTransaction(USER_ID, transactionData);

        verify(transactionCacheService).addTransaction(eq(transactionData), any(String.class), eq(30L));
        verify(userTransactionCacheService).addTransactionToUser(eq(USER_ID), any(String.class), eq(30L));
    }
}
