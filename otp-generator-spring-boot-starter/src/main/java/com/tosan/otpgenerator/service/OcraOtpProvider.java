package com.tosan.otpgenerator.service;

import com.tosan.otpgenerator.config.OtpProperties;
import com.tosan.otpgenerator.exception.OtpException;
import com.tosan.otpgenerator.model.OtpData;
import com.tosan.otpgenerator.model.OtpRequest;
import com.tosan.otpgenerator.model.OtpVerificationRequest;
import com.tosan.otpgenerator.service.cache.OtpConsumptionMarkerService;
import com.tosan.otpgenerator.service.cache.UserTransactionManager;
import com.tosan.otpgenerator.utils.OtpUtil;
import com.tosan.otpgenerator.utils.OtpValidator;
import com.tosan.otpgenerator.utils.TimeStepUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
@Slf4j
public class OcraOtpProvider {

    private final OtpProperties properties;
    private final UserTransactionManager userTransactionManager;
    private final OtpUtil otpUtil;
    private final SecretKeyProvider secretKeyProvider;
    private final OtpValidator otpValidator;
    private final TimeStepUtil timeStepUtil;
    private final OtpConsumptionMarkerService consumptionMarker;

    public OcraOtpProvider(OtpProperties properties,
                           UserTransactionManager userTransactionManager,
                           OtpUtil otpUtil,
                           SecretKeyProvider secretKeyProvider,
                           OtpValidator otpValidator,
                           TimeStepUtil timeStepUtil,
                           OtpConsumptionMarkerService consumptionMarker) {
        this.properties = properties;
        this.userTransactionManager = userTransactionManager;
        this.otpUtil = otpUtil;
        this.secretKeyProvider = secretKeyProvider;
        this.otpValidator = otpValidator;
        this.timeStepUtil = timeStepUtil;
        this.consumptionMarker = consumptionMarker;
    }

    public String generate(OtpRequest request) {

        otpValidator.validateGenerate(request);

        long timeCounter = timeStepUtil.currentTimeStep(properties.getTimeStepSeconds());

        try {
            byte[] secretKey = secretKeyProvider.getSecretKey(request.getUserIdentifier());
            log.debug("Generating OTP for userId={} at timeCounter={}", request.getUserIdentifier(), timeCounter);

            OtpData otpData = new OtpData();
            otpData.setOtpMapData(request.getOtpData());
            otpData.setOtpLength(request.getOtpLength());

            long issuanceSequence = userTransactionManager.addTransaction(request.getUserIdentifier(), otpData);
            String transactionDataString = otpUtil.buildTransactionDataString(otpData);

            String generatedOtp = generateOtp(
                    secretKey, transactionDataString, timeCounter, request.getOtpLength(), issuanceSequence);

            if (generatedOtp == null || generatedOtp.isBlank()) {
                log.warn("Generated OTP is empty for userId={}", request.getUserIdentifier());
                throw new OtpException("Generated OTP is null ");
            }

            return generatedOtp;

        } catch (OtpException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Failed to generate OTP for userId={}", request.getUserIdentifier(), ex);
            throw new OtpException("Failed to generate OTP", ex);
        }
    }

    public void validate(OtpVerificationRequest request) {

        otpValidator.validateOtpVerification(request);

        String userId = request.getUserIdentifier();

        OtpData otpData = new OtpData();
        otpData.setOtpMapData(request.getOtpData());
        otpData.setOtpLength(request.getOtpLength());

        String transactionId = otpUtil.buildTransactionDataString(otpData);

        OtpData cachedTransaction = userTransactionManager.getRegisteredTransaction(userId, transactionId);
        if (cachedTransaction == null) {
            throw new OtpException("Transaction expired or not found");
        }

        long issuanceSequence = resolveIssuanceSequence(cachedTransaction);
        consumptionMarker.checkOtpConsumption(userId, transactionId, issuanceSequence);

        String transactionDataString = otpUtil.buildTransactionDataString(cachedTransaction);

        if (!validateOtp(request, userId, transactionDataString, issuanceSequence)) {
            throw new OtpException("Invalid OTP");
        }

        consumptionMarker.markOtpConsumed(userId, transactionId, issuanceSequence);
        userTransactionManager.unlinkTransactionFromUser(userId, transactionId);
    }

    private boolean validateOtp(OtpVerificationRequest request,
                                String userId,
                                String transactionDataString,
                                long issuanceSequence) {

        try {
            long currentStep = timeStepUtil.currentTimeStep(properties.getTimeStepSeconds());
            byte[] secretKey = secretKeyProvider.getSecretKey(userId);

            for (long step = -properties.getAllowedClockSkew(); step <= properties.getAllowedClockSkew(); step++) {
                if (matchesSubmittedOtp(
                        secretKey,
                        transactionDataString,
                        currentStep + step,
                        request.getOtp(),
                        request.getOtpLength(),
                        issuanceSequence)) {
                    return true;
                }
            }
            return false;

        } catch (Exception ex) {
            return false;
        }
    }

    private boolean matchesSubmittedOtp(byte[] secretKey,
                                        String otpData,
                                        long timeCounter,
                                        String submittedOtp,
                                        int otpLength,
                                        long issuanceSequence) {

        String candidate = generateOtp(secretKey, otpData, timeCounter, otpLength, issuanceSequence);
        return MessageDigest.isEqual(candidate.getBytes(StandardCharsets.UTF_8),
                submittedOtp.getBytes(StandardCharsets.UTF_8));
    }

    private long resolveIssuanceSequence(OtpData cachedTransaction) {

        Long issuanceSequence = cachedTransaction.getIssuanceSequence();
        return issuanceSequence == null ? 1L : issuanceSequence;
    }

    private String generateOtp(byte[] secretKey,
                                 String otpData,
                                 long timeCounter,
                                 int otpLength,
                                 long issuanceSequence) {

        String otpPayload = otpUtil.buildOtpPayload(otpData, timeCounter, issuanceSequence);

        byte[] hash = otpUtil.generateHmac(
                properties.getCryptoAlgorithm(),
                secretKey,
                otpPayload.getBytes(StandardCharsets.UTF_8));

        return otpUtil.truncate(hash, otpLength);
    }
}
