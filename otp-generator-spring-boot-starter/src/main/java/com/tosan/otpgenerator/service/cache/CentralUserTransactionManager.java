package com.tosan.otpgenerator.service.cache;

import com.tosan.otpgenerator.config.OtpProperties;
import com.tosan.otpgenerator.model.OtpData;
import com.tosan.otpgenerator.utils.OtpUtil;
import com.tosan.tools.lockmanager.api.LockManagementService;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
public class CentralUserTransactionManager extends AbstractUserTransactionManager {

    private static final String USER_TRANSACTION_LOCK_TYPE = "OTP_USER_TRANSACTION";
    private static final int LOCK_TIMEOUT_SECONDS = 60;

    private final LockManagementService lockManagementService;
    private final OtpProperties otpProperties;


    public CentralUserTransactionManager(UserTransactionCacheService userTransactionCacheService,
                                         TransactionCacheService transactionCacheService,
                                         LockManagementService lockManagementService,
                                         OtpProperties otpProperties,
                                         OtpUtil otpUtil) {

        super(userTransactionCacheService, transactionCacheService, otpUtil);

        this.lockManagementService = lockManagementService;
        this.otpProperties = otpProperties;
    }

    @Override
    public void addTransaction(String userId, OtpData otpData) {

        String transactionDataString = otpUtil.buildTransactionDataString(otpData);
        String transactionUserKey = otpUtil.buildTransactionKey(transactionDataString, userId);

        long ttl = otpProperties.getTimeStepSeconds();

        lockManagementService.requestWriteLock(USER_TRANSACTION_LOCK_TYPE, userId, LOCK_TIMEOUT_SECONDS, false);

        try {
            transactionCacheService.addTransaction(otpData, transactionUserKey, ttl);
            userTransactionCacheService.addTransactionToUser(userId, transactionUserKey, ttl);
        } finally {
            lockManagementService.unlock(USER_TRANSACTION_LOCK_TYPE, userId);
        }
    }

    @Override
    public void unlinkTransactionFromUser(String userId, String transactionId) {

        String transactionUserKey = otpUtil.buildTransactionKey(transactionId, userId);

        lockManagementService.requestWriteLock(USER_TRANSACTION_LOCK_TYPE, userId, LOCK_TIMEOUT_SECONDS, false);
        try {
            transactionCacheService.removeTransaction(transactionUserKey);
            userTransactionCacheService.removeTransaction(userId, transactionUserKey);

        } finally {

            lockManagementService.unlock(USER_TRANSACTION_LOCK_TYPE, userId);
        }
    }
}

