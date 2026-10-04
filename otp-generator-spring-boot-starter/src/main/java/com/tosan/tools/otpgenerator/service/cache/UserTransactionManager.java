package com.tosan.tools.otpgenerator.service.cache;

import com.tosan.tools.otpgenerator.model.OtpData;

import java.util.List;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
public interface UserTransactionManager {

    void addTransaction(String userId, OtpData otpData);

    void unlinkTransactionFromUser(String userId, String transactionId);

    List<OtpData> getTransactions(String userId);

    OtpData getRegisteredTransaction(String userId, String transactionId);

    Long getRemainingTtlSeconds(String userId, OtpData otpData);
}

