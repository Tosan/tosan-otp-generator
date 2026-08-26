package com.tosan.otpgenerator.otpgenerator.service.cache;

import com.tosan.otpgenerator.otpgenerator.AbstractUTest;
import com.tosan.otpgenerator.service.cache.OtpCacheInitializer;
import org.junit.jupiter.api.BeforeEach;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
public abstract class AbstractOtpCacheInitializerUTest extends AbstractUTest {

    protected OtpCacheInitializer otpCacheInitializer;

    @BeforeEach
    void otpCacheInitializerSetUp() {
        otpCacheInitializer = createOtpCacheInitializer();
    }

    protected OtpCacheInitializer createOtpCacheInitializer() {
        return new OtpCacheInitializer(cacheManager);
    }
}
