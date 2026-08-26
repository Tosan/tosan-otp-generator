package com.tosan.otpgenerator.otpgenerator.service.cache;

import com.tosan.otpgenerator.model.OtpData;
import com.tosan.otpgenerator.otpgenerator.TestFixtures;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
class TransactionCacheService_getTransactionsUTest extends AbstractTransactionCacheServiceUTest {

    @Test
    void multipleExistingKeys_returnsAllTransactions() {

        String keyOne = transactionCacheKey("tx-1", USER_ID);
        String keyTwo = transactionCacheKey("tx-2", USER_ID);
        var transactionOne = TestFixtures.otpData("tx-1", "acc-1", new BigDecimal("10.00"), "USD");
        var transactionTwo = TestFixtures.otpData("tx-2", "acc-2", new BigDecimal("20.00"), "EUR");

        when(cacheManager.getItemFromCache("TRANSACTION", keyOne)).thenReturn(transactionOne);
        when(cacheManager.getItemFromCache("TRANSACTION", keyTwo)).thenReturn(transactionTwo);

        List<OtpData> result = transactionCacheService.getTransactions(Set.of(keyOne, keyTwo));

        assertEquals(2, result.size());
        assertTrue(result.contains(transactionOne));
        assertTrue(result.contains(transactionTwo));
    }

    @Test
    void someTransactionNotExist_returnsOnlyExistingTransactions() {

        String existingKey = transactionCacheKey("tx-1", USER_ID);
        String missingKey = transactionCacheKey("tx-missing", USER_ID);
        var transaction = TestFixtures.otpData("tx-1", "acc-1", new BigDecimal("10.00"), "USD");

        when(cacheManager.getItemFromCache("TRANSACTION", existingKey)).thenReturn(transaction);
        when(cacheManager.getItemFromCache("TRANSACTION", missingKey)).thenReturn(null);

        List<OtpData> result = transactionCacheService.getTransactions(Set.of(existingKey, missingKey));

        assertEquals(1, result.size());
        assertEquals(transaction, result.getFirst());
    }

    @Test
    void emptyKeySet_returnsEmptyList() {

        List<OtpData> result = transactionCacheService.getTransactions(Collections.emptySet());
        assertTrue(result.isEmpty());
    }
}
