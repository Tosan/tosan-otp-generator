package com.tosan.otpgenerator.otpgenerator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tosan.client.redis.api.TedissonCacheManager;
import com.tosan.otpgenerator.config.OtpProperties;
import com.tosan.otpgenerator.utils.OtpUtil;
import com.tosan.otpgenerator.utils.OtpValidator;
import com.tosan.otpgenerator.utils.TimeStepUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
@ExtendWith(MockitoExtension.class)
public abstract class AbstractUTest {

    protected static final String USER_ID = "user-1";
    protected static final String TRANSACTION_ID = "tx-1";
    protected static final Instant FIXED_INSTANT = Instant.parse("2024-01-01T00:00:30Z");

    @Mock
    protected TedissonCacheManager cacheManager;

    protected ObjectMapper objectMapper;
    protected OtpProperties otpProperties;
    protected OtpUtil realOtpUtil;
    protected OtpValidator otpValidator;
    protected TimeStepUtil timeStepUtil;
    protected Clock fixedClock;

    @BeforeEach
    void baseSetUp() {
        objectMapper = new ObjectMapper();
        otpProperties = TestFixtures.validOtpProperties();
        realOtpUtil = new OtpUtil(objectMapper);
        otpValidator = new OtpValidator();
        fixedClock = Clock.fixed(FIXED_INSTANT, ZoneOffset.UTC);
        timeStepUtil = new TimeStepUtil(fixedClock);
    }

    protected String transactionCacheKey(String transactionId, String userId) {
        return realOtpUtil.buildTransactionKey(transactionId, userId);
    }
}
