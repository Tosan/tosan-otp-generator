package com.tosan.otpgenerator.otpgenerator.service.cache;

import com.tosan.otpgenerator.otpgenerator.AbstractUTest;
import com.tosan.otpgenerator.service.cache.OtpConsumptionMarkerService;
import com.tosan.otpgenerator.service.cache.UserTransactionManager;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
public abstract class AbstractOtpConsumptionMarkerServiceUTest extends AbstractUTest {

    @Mock
    protected UserTransactionManager userTransactionManager;

    protected OtpConsumptionMarkerService otpConsumptionMarker;

    @BeforeEach
    void otpConsumptionMarkerSetUp() {
        otpConsumptionMarker = createOtpConsumptionMarker();
    }

    protected OtpConsumptionMarkerService createOtpConsumptionMarker() {
        return new OtpConsumptionMarkerService(
                cacheManager,
                otpProperties,
                userTransactionManager,
                realOtpUtil
        );
    }

    protected String consumptionCacheKey() {
        return realOtpUtil.buildTransactionKey(AbstractUTest.USER_ID, AbstractUTest.TRANSACTION_ID);
    }
}
