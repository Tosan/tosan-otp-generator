package com.tosan.tools.otpgenerator.otpgenerator.service.cache;

import com.tosan.tools.otpgenerator.otpgenerator.AbstractUTest;
import com.tosan.tools.otpgenerator.service.cache.UserTransactionCacheService;
import org.junit.jupiter.api.BeforeEach;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
public abstract class AbstractUserTransactionCacheServiceUTest extends AbstractUTest {

    protected UserTransactionCacheService userTransactionCacheService;

    @BeforeEach
    void userTransactionCacheServiceSetUp() {
        userTransactionCacheService = createUserTransactionCacheService();
    }

    protected UserTransactionCacheService createUserTransactionCacheService() {
        return new UserTransactionCacheService(cacheManager);
    }
}
