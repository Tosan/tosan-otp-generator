package com.tosan.otpgenerator.service.cache;

import com.tosan.otpgenerator.model.OtpData;
import com.tosan.otpgenerator.utils.OtpUtil;

import java.util.*;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
public abstract class AbstractUserTransactionManager implements UserTransactionManager {

    protected final UserTransactionCacheService userTransactionCacheService;
    protected final TransactionCacheService transactionCacheService;
    protected final OtpUtil otpUtil;

    public AbstractUserTransactionManager(UserTransactionCacheService userTransactionCacheService, TransactionCacheService transactionCacheService, OtpUtil otpUtil) {

        this.userTransactionCacheService = userTransactionCacheService;
        this.transactionCacheService = transactionCacheService;
        this.otpUtil = otpUtil;
    }

    @Override
    public List<OtpData> getTransactions(String userId) {

        Set<String> transactions = Optional.ofNullable(userTransactionCacheService.getTransactionIds(userId))
                .orElse(Collections.emptySet());

        if (transactions.isEmpty()) {
            return Collections.emptyList();
        }

        return transactionCacheService.getTransactions(transactions);
    }

    @Override
    public OtpData getRegisteredTransaction(String userId, String transactionId) {

        String transactionUserKey = otpUtil.buildTransactionKey(transactionId, userId);
        return transactionCacheService.getTransaction(transactionUserKey);
    }

    @Override
    public Long getRemainingTtlSeconds(String userId, OtpData otpData) {

        String transactionId = otpUtil.buildTransactionDataString(otpData);
        String transactionUserKey = otpUtil.buildTransactionKey(transactionId, userId);
        return transactionCacheService.getRemainingTtlSeconds(transactionUserKey);
    }
}

