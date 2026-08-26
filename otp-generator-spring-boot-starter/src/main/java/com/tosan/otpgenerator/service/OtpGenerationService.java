package com.tosan.otpgenerator.service;

import com.tosan.otpgenerator.model.OtpRequest;
import com.tosan.otpgenerator.model.OtpVerificationRequest;
import com.tosan.otpgenerator.model.TransactionDataDto;
import com.tosan.otpgenerator.service.cache.UserTransactionManager;
import com.tosan.otpgenerator.utils.OtpUtil;

import java.util.List;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
public class OtpGenerationService {

    private final UserTransactionManager userTransactionManager;
    private final OtpUtil otpUtil;
    private final OcraOtpProvider ocraOtpProvider;

    public OtpGenerationService(UserTransactionManager userTransactionManager, OtpUtil otpUtil, OcraOtpProvider ocraOtpProvider) {

        this.userTransactionManager = userTransactionManager;
        this.otpUtil = otpUtil;
        this.ocraOtpProvider = ocraOtpProvider;
    }

    public String generate(OtpRequest otpRequest) {

        return ocraOtpProvider.generate(otpRequest);
    }

    public void validate(OtpVerificationRequest otpValidationBaseModel) {

        ocraOtpProvider.validate(otpValidationBaseModel);
    }

    public List<TransactionDataDto> getUserTransactions(String userId) {

        return userTransactionManager.getTransactions(userId)
                .stream()
                .map(transaction -> new TransactionDataDto(
                        transaction,
                        otpUtil.buildTransactionDataString(transaction),
                        userTransactionManager.getRemainingTtlSeconds(userId, transaction)
                ))
                .toList();
    }
}
