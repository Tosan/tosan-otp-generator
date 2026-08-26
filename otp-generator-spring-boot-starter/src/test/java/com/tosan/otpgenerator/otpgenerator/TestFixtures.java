package com.tosan.otpgenerator.otpgenerator;

import com.tosan.otpgenerator.config.OtpProperties;
import com.tosan.otpgenerator.model.OtpData;
import com.tosan.otpgenerator.model.OtpRequest;
import com.tosan.otpgenerator.model.OtpVerificationRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
public final class TestFixtures {

    public static OtpProperties validOtpProperties() {
        OtpProperties properties = new OtpProperties();
        properties.setSuite("OCRA-1:HOTP-SHA256-6:TX-T30S");
        properties.setTimeStepSeconds(30);
        properties.setCryptoAlgorithm("HmacSHA1");
        properties.setAllowedClockSkew(1);
        return properties;
    }

    public static byte[] validSecretKey() {
        return "test-secret-key".getBytes();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestOtpData extends OtpData {
        private String dataIdentifier;
        private String accountNumber;
        private BigDecimal amount;
        private String currency;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestOtpRequest implements OtpRequest {
        private String userIdentifier;
        private TestOtpData otpData;
        private Integer otpLength;

        @Override
        public java.util.Map<String, Object> getOtpData() {
            return java.util.Map.of(
                    "dataIdentifier", otpData.getDataIdentifier(),
                    "accountNumber", otpData.getAccountNumber(),
                    "amount", otpData.getAmount(),
                    "currency", otpData.getCurrency()
            );
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestOtpVerificationRequest implements OtpVerificationRequest {
        private String userIdentifier;
        private TestOtpData otpData;
        private Integer otpLength;
        private String otp;

        @Override
        public java.util.Map<String, Object> getOtpData() {
            return java.util.Map.of(
                    "dataIdentifier", otpData.getDataIdentifier(),
                    "accountNumber", otpData.getAccountNumber(),
                    "amount", otpData.getAmount(),
                    "currency", otpData.getCurrency()
            );
        }
    }

    public static TestOtpData otpData(String transactionId, String accountNumber,
                                      BigDecimal amount, String currency) {
        return new TestOtpData(transactionId, accountNumber, amount, currency);
    }

    public static TestOtpRequest otpRequest(String userId, TestOtpData testOtpData) {
        return otpRequest(userId, testOtpData, 6);
    }

    public static TestOtpRequest otpRequest(String userId, TestOtpData testOtpData, Integer otpLength) {
        return new TestOtpRequest(userId, testOtpData, otpLength);
    }

    public static TestOtpVerificationRequest otpVerificationRequest(String userId,
                                                                    TestOtpData testOtpData,
                                                                    String otp) {
        return otpVerificationRequest(userId, testOtpData, otp, 6);
    }

    public static TestOtpVerificationRequest otpVerificationRequest(String userId,
                                                                    TestOtpData testOtpData,
                                                                    String otp,
                                                                    Integer otpLength) {
        return new TestOtpVerificationRequest(userId, testOtpData, otpLength, otp);
    }
}
