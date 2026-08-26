package com.tosan.otpgenerator.utils;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tosan.otpgenerator.exception.OtpException;
import com.tosan.otpgenerator.model.OtpData;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
public class OtpUtil {

    private static final String KEY_SEPARATOR = "_";

    private final ObjectMapper objectMapper;

    public OtpUtil(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public byte[] generateHmac(String algorithm, byte[] key, byte[] payload) {
        try {
            Mac mac = Mac.getInstance(algorithm);
            mac.init(new SecretKeySpec(key, algorithm));
            return mac.doFinal(payload);
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            throw new OtpException(
                    "Failed to compute OTP using configured HMAC algorithm", ex);
        }
    }

    public String buildTransactionDataString(OtpData otpData) {

        Map<String, Object> map = objectMapper.convertValue(otpData,
                new TypeReference<>() {
                }
        );
        return map.entrySet().stream()
                .filter(e -> e.getValue() != null)
                .sorted(Map.Entry.comparingByKey())
                .map(e -> encode(e.getKey()) + "=" + encode(String.valueOf(e.getValue())))
                .collect(Collectors.joining("&"));
    }

    public String buildOtpPayload(String transactionPart, long timeCounter) {

        return timeCounter + "|" + transactionPart;
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    public String truncate(byte[] hash, int digits) {
        if (hash.length < 20) {
            throw new OtpException("HMAC output is too short for dynamic truncation");
        }

        int offset = hash[hash.length - 1] & 0x0F;
        int binary = ((hash[offset] & 0x7F) << 24)
                | ((hash[offset + 1] & 0xFF) << 16)
                | ((hash[offset + 2] & 0xFF) << 8)
                | (hash[offset + 3] & 0xFF);

        int otp = binary % (int) Math.pow(10, digits);
        return String.format("%0" + digits + "d", otp);
    }

    public String buildTransactionKey(String userId, String transactionId) {

        return userId + KEY_SEPARATOR + transactionId;
    }
}
