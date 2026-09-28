package com.tosan.otpgenerator.service.cache;

import com.tosan.client.redis.api.TedissonCacheManager;
import com.tosan.otpgenerator.config.OtpProperties;
import com.tosan.otpgenerator.exception.OtpException;
import com.tosan.otpgenerator.service.enums.CacheName;
import com.tosan.otpgenerator.utils.OtpUtil;

import java.util.concurrent.TimeUnit;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
public class OtpConsumptionMarkerService {

    private static final String CONSUMED_CACHE = CacheName.OTP_CONSUMED.name();

    private final TedissonCacheManager cacheManager;
    private final OtpProperties properties;
    private final OtpUtil otpUtil;

    public OtpConsumptionMarkerService(TedissonCacheManager cacheManager,
                                       OtpProperties properties,
                                       OtpUtil otpUtil) {
        this.cacheManager = cacheManager;
        this.properties = properties;
        this.otpUtil = otpUtil;
    }

    public long getConsumedCount(String userId, String transactionId) {

        Number count = cacheManager.getItemFromCache(CONSUMED_CACHE, otpUtil.buildTransactionKey(userId, transactionId));
        return count == null ? 0 : count.longValue();
    }

    public void checkOtpConsumption(String userId, String transactionId, long issuanceSequence) {

        if (getConsumedCount(userId, transactionId) >= issuanceSequence) {
            throw new OtpException("OTP already used");
        }
    }


    public void markOtpConsumed(String userId, String transactionId, long issuanceSequence) {

        String key = otpUtil.buildTransactionKey(userId, transactionId);
        long count = cacheManager.incrementAndGetAtomicItem(CONSUMED_CACHE, key);

        long markerTtlSeconds = properties.getTimeStepSeconds() * (1 + properties.getAllowedClockSkew());
        cacheManager.expireAtomicItem(
                CONSUMED_CACHE, key, markerTtlSeconds, TimeUnit.SECONDS);

        if (count > issuanceSequence) {
            throw new OtpException("OTP already used or expired");
        }

        cacheManager.addItemToCache(CONSUMED_CACHE, key, count, markerTtlSeconds, TimeUnit.SECONDS);
    }

}
