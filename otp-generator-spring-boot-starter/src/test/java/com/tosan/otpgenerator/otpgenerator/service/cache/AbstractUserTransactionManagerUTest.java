package com.tosan.otpgenerator.otpgenerator.service.cache;

import com.tosan.otpgenerator.otpgenerator.AbstractUTest;
import com.tosan.otpgenerator.otpgenerator.TestFixtures;
import com.tosan.otpgenerator.otpgenerator.TestFixtures.TestOtpData;
import com.tosan.otpgenerator.service.cache.LocalUserTransactionManager;
import com.tosan.otpgenerator.service.cache.TransactionCacheService;
import com.tosan.otpgenerator.service.cache.UserTransactionCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

import java.math.BigDecimal;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
public abstract class AbstractUserTransactionManagerUTest extends AbstractUTest {

    @Mock
    protected UserTransactionCacheService userTransactionCacheService;

    @Mock
    protected TransactionCacheService transactionCacheService;

    protected LocalUserTransactionManager userTransactionManager;

    @BeforeEach
    void userTransactionManagerSetUp() {
        userTransactionManager = new LocalUserTransactionManager(
                userTransactionCacheService, transactionCacheService, otpProperties, realOtpUtil);
    }

    protected TestOtpData sampleTransactionData() {
        return TestFixtures.otpData(
                TRANSACTION_ID, "acc-123", new BigDecimal("100.00"), "USD");
    }
}
