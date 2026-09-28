package com.tosan.otpgenerator.service.cache;

import com.tosan.otpgenerator.config.OtpProperties;
import com.tosan.otpgenerator.model.OtpData;
import com.tosan.otpgenerator.utils.OtpUtil;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
public class LocalUserTransactionManager extends AbstractUserTransactionManager {

    private final OtpProperties otpProperties;

    private final ConcurrentMap<String, Object> userLocks = new ConcurrentHashMap<>();

    private Object getUserLock(String userId) {

        return userLocks.computeIfAbsent(userId, k -> new Object());
    }

    public LocalUserTransactionManager(UserTransactionCacheService userTransactionCacheService,
                                       TransactionCacheService transactionCacheService,
                                       OtpProperties otpProperties,
                                       OtpUtil otpUtil,
                                       OtpConsumptionMarkerService consumptionMarker) {

        super(userTransactionCacheService, transactionCacheService, otpUtil, consumptionMarker);
        this.otpProperties = otpProperties;
    }

    @Override
    public long addTransaction(String userId, OtpData otpData) {

        long ttl = otpProperties.getTimeStepSeconds();
        synchronized (getUserLock(userId)) {
            return registerOrReissueTransaction(userId, otpData, ttl);
        }
    }

    @Override
    public void unlinkTransactionFromUser(String userId, String transactionId) {

        String transactionUserKey = otpUtil.buildTransactionKey(userId, transactionId);

        synchronized (getUserLock(userId)) {
            transactionCacheService.removeTransaction(transactionUserKey);
            userTransactionCacheService.removeTransaction(userId, transactionUserKey);
        }
    }
}
