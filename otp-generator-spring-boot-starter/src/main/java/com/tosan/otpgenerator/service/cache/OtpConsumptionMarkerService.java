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
    private final UserTransactionManager userTransactionManager;
    private final OtpUtil otpUtil;

    public OtpConsumptionMarkerService(TedissonCacheManager cacheManager,
                                       OtpProperties properties,
                                       UserTransactionManager userTransactionManager,
                                       OtpUtil otpUtil) {
        this.cacheManager = cacheManager;
        this.properties = properties;
        this.userTransactionManager = userTransactionManager;
        this.otpUtil = otpUtil;
    }

    public void checkOtpConsumption(String userId, String transactionId) {

        if (cacheManager.getAtomicValue(CONSUMED_CACHE, otpUtil.buildTransactionKey(userId, transactionId)) >= 1) {
            throw new OtpException("OTP already used or expired");
        }
    }


    public void markOtpConsumed(String userId, String transactionId) {

        String key = otpUtil.buildTransactionKey(userId, transactionId);
        long count = cacheManager.incrementAndGetAtomicItem(CONSUMED_CACHE, key);

        if (count == 1) {
            cacheManager.expireAtomicItem(
                    CONSUMED_CACHE, key, properties.getTimeStepSeconds(), TimeUnit.SECONDS);
        }

        if (count > 1) {
            throw new OtpException("OTP already used or expired");
        }
        userTransactionManager.unlinkTransactionFromUser(userId, transactionId);
    }

}
