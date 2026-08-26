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
                                       OtpUtil otpUtil) {

        super(userTransactionCacheService, transactionCacheService, otpUtil);
        this.otpProperties = otpProperties;
    }

    @Override
    public void addTransaction(String userId, OtpData otpData) {

        String transactionDataString = otpUtil.buildTransactionDataString(otpData);
        String transactionUserKey = otpUtil.buildTransactionKey(transactionDataString, userId);
        long ttl = otpProperties.getTimeStepSeconds();

        synchronized (getUserLock(userId)) {
            transactionCacheService.addTransaction(otpData, transactionUserKey, ttl);
            userTransactionCacheService.addTransactionToUser(userId, transactionUserKey, ttl);
        }
    }

    @Override
    public void unlinkTransactionFromUser(String userId, String transactionId) {

        String transactionUserKey = otpUtil.buildTransactionKey(transactionId, userId);

        synchronized (getUserLock(userId)) {
            transactionCacheService.removeTransaction(transactionUserKey);
            userTransactionCacheService.removeTransaction(userId, transactionUserKey);
        }
    }
}

