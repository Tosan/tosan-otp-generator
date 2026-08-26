package com.tosan.otpgenerator.otpgenerator.service.cache;

import com.tosan.otpgenerator.model.OtpData;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
class UserTransactionManager_getTransactionsUTest extends AbstractUserTransactionManagerUTest {

    @Test
    void userWithExistingTransactions_returnsTransactionDataList() {

        String key = transactionCacheKey(TRANSACTION_ID, USER_ID);
        var transactionData = sampleTransactionData();

        when(userTransactionCacheService.getTransactionIds(USER_ID)).thenReturn(Set.of(key));
        when(transactionCacheService.getTransactions(Set.of(key))).thenReturn(List.of(transactionData));

        List<OtpData> result = userTransactionManager.getTransactions(USER_ID);

        assertEquals(1, result.size());
        assertEquals(transactionData, result.getFirst());
    }

    @Test
    void userWithNoTransactions_returnsEmptyList() {

        when(userTransactionCacheService.getTransactionIds(USER_ID)).thenReturn(Set.of());

        List<OtpData> result = userTransactionManager.getTransactions(USER_ID);

        assertTrue(result.isEmpty());
    }

    @Test
    void someTransactionsExpired_returnsOnlyNonExpiredTransactions() {

        String activeKey = transactionCacheKey("tx-active", USER_ID);
        String expiredKey = transactionCacheKey("tx-expired", USER_ID);
        var activeTransaction = com.tosan.otpgenerator.otpgenerator.TestFixtures.otpData(
                "tx-active", "acc-123", new BigDecimal("100.00"), "USD");

        when(userTransactionCacheService.getTransactionIds(USER_ID))
                .thenReturn(Set.of(activeKey, expiredKey));
        when(transactionCacheService.getTransactions(Set.of(activeKey, expiredKey)))
                .thenReturn(List.of(activeTransaction));

        List<OtpData> result = userTransactionManager.getTransactions(USER_ID);

        assertEquals(1, result.size());
        assertEquals(activeTransaction, result.getFirst());
    }
}
