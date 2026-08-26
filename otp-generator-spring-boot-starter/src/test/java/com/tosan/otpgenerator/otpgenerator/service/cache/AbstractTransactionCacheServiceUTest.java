package com.tosan.otpgenerator.otpgenerator.service.cache;

import com.tosan.otpgenerator.otpgenerator.AbstractUTest;
import com.tosan.otpgenerator.service.cache.TransactionCacheService;
import org.junit.jupiter.api.BeforeEach;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
public abstract class AbstractTransactionCacheServiceUTest extends AbstractUTest {

    protected TransactionCacheService transactionCacheService;

    @BeforeEach
    void transactionCacheServiceSetUp() {
        transactionCacheService = createTransactionCacheService();
    }

    protected TransactionCacheService createTransactionCacheService() {
        return new TransactionCacheService(cacheManager);
    }
}
