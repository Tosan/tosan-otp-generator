package com.tosan.otpgenerator.otpgenerator.service.cache;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.verify;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
class TransactionCacheService_removeTransactionUTest extends AbstractTransactionCacheServiceUTest {

    @Test
    void existingTransaction_removesFromCache() {

        String cacheKey = transactionCacheKey(TRANSACTION_ID, USER_ID);

        transactionCacheService.removeTransaction(cacheKey);

        verify(cacheManager).removeItemFromCache("TRANSACTION", cacheKey);
    }
}
