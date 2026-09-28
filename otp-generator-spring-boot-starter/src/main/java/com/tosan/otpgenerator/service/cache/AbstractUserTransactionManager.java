package com.tosan.otpgenerator.service.cache;

import com.tosan.otpgenerator.exception.OtpException;
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
    protected final OtpConsumptionMarkerService consumptionMarker;

    public AbstractUserTransactionManager(UserTransactionCacheService userTransactionCacheService,
                                          TransactionCacheService transactionCacheService,
                                          OtpUtil otpUtil,
                                          OtpConsumptionMarkerService consumptionMarker) {

        this.userTransactionCacheService = userTransactionCacheService;
        this.transactionCacheService = transactionCacheService;
        this.otpUtil = otpUtil;
        this.consumptionMarker = consumptionMarker;
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

        String transactionUserKey = otpUtil.buildTransactionKey(userId, transactionId);
        return transactionCacheService.getTransaction(transactionUserKey);
    }

    @Override
    public Long getRemainingTtlSeconds(String userId, OtpData otpData) {

        String transactionId = otpUtil.buildTransactionDataString(otpData);
        String transactionUserKey = otpUtil.buildTransactionKey(userId,transactionId);
        return transactionCacheService.getRemainingTtlSeconds(transactionUserKey);
    }

    protected long registerOrReissueTransaction(String userId, OtpData otpData, long ttlSeconds) {

        String transactionDataString = otpUtil.buildTransactionDataString(otpData);
        String transactionUserKey = otpUtil.buildTransactionKey(userId,transactionDataString);
        OtpData current = transactionCacheService.getTransaction(transactionUserKey);

        long consumedCount = consumptionMarker.getConsumedCount(userId, transactionDataString);

        if (current != null && consumedCount < issuanceSequenceOf(current)) {
            throw new OtpException("Generated Otp is not expired yet!");
        }

        long issuanceSequence = consumedCount + 1;
        otpData.setIssuanceSequence(issuanceSequence);

        if (current == null) {
            transactionCacheService.addTransaction(otpData, transactionUserKey, ttlSeconds);
            userTransactionCacheService.addTransactionToUser(userId, transactionUserKey, ttlSeconds);
        } else {
            transactionCacheService.replaceTransaction(otpData, transactionUserKey, ttlSeconds);
        }
        return issuanceSequence;
    }

    private long issuanceSequenceOf(OtpData otpData) {

        Long issuanceSequence = otpData.getIssuanceSequence();
        return issuanceSequence == null ? 1L : issuanceSequence;
    }
}
