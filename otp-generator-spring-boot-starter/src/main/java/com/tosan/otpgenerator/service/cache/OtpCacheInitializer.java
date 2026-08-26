package com.tosan.otpgenerator.service.cache;

import com.tosan.client.redis.api.TedissonCacheManager;
import com.tosan.client.redis.cacheconfig.CacheConfig;
import com.tosan.otpgenerator.service.enums.CacheName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.util.Arrays;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
@Slf4j
public class OtpCacheInitializer {

    private static final int CACHE_MAX_SIZE = 10_000;

    private final TedissonCacheManager cacheManager;

    public OtpCacheInitializer(TedissonCacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initCaches() {

        log.info("Starting OTP cache initialization...");
        Arrays.stream(CacheName.values()).forEach(cacheName -> {
            try {
                cacheManager.createCache(cacheName.name(), cacheConfig());
                log.info("Successfully initialized cache: [{}]", cacheName.name());
            } catch (Exception ex) {
                log.error("Failed to initialize cache: [{}]. Error: {}", cacheName.name(), ex.getMessage(), ex);
            }
        });
    }

    private CacheConfig cacheConfig() {
        CacheConfig config = new CacheConfig();
        config.setMaxSize(CACHE_MAX_SIZE);
        return config;
    }
}
