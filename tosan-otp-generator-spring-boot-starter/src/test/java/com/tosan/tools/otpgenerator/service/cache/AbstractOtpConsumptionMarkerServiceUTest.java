package com.tosan.tools.otpgenerator.service.cache;

import com.tosan.tools.otpgenerator.AbstractUTest;
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
                realOtpUtil
        );
    }

    protected String consumptionCacheKey() {
        return realOtpUtil.buildTransactionKey(AbstractUTest.USER_ID, AbstractUTest.TRANSACTION_ID);
    }
}
