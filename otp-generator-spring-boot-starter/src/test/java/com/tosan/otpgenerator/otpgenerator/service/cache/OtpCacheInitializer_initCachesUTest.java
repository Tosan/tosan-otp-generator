package com.tosan.otpgenerator.otpgenerator.service.cache;

import com.tosan.client.redis.cacheconfig.CacheConfig;
import com.tosan.otpgenerator.service.enums.CacheName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
@ExtendWith(OutputCaptureExtension.class)
class OtpCacheInitializer_initCachesUTest extends AbstractOtpCacheInitializerUTest {

    @Test
    void applicationReadyEvent_createsAllThreeCachesSuccessfully() {

        otpCacheInitializer.initCaches();

        verify(cacheManager).createCache(eq(CacheName.USER.name()), any(CacheConfig.class));
        verify(cacheManager).createCache(eq(CacheName.TRANSACTION.name()), any(CacheConfig.class));
        verify(cacheManager).createCache(eq(CacheName.OTP_CONSUMED.name()), any(CacheConfig.class));
    }

    @Test
    void oneCacheInitializationFails_logsErrorAndContinuesWithOtherCaches(CapturedOutput output) {

        doAnswer(invocation -> {
            String cacheName = invocation.getArgument(0);
            if (CacheName.USER.name().equals(cacheName)) {
                throw new RuntimeException("cache init failed");
            }
            return null;
        }).when(cacheManager).createCache(any(), any());

        otpCacheInitializer.initCaches();

        verify(cacheManager).createCache(eq(CacheName.USER.name()), any(CacheConfig.class));
        verify(cacheManager).createCache(eq(CacheName.TRANSACTION.name()), any(CacheConfig.class));
        verify(cacheManager).createCache(eq(CacheName.OTP_CONSUMED.name()), any(CacheConfig.class));
        assertTrue(output.getOut().contains("Failed to initialize cache: [USER]"));
    }
}
