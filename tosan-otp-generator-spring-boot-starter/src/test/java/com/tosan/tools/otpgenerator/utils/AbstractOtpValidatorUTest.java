package com.tosan.tools.otpgenerator.utils;

import com.tosan.tools.otpgenerator.AbstractUTest;
import com.tosan.tools.otpgenerator.TestFixtures;
import com.tosan.tools.otpgenerator.TestFixtures.TestOtpData;

import java.math.BigDecimal;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
public abstract class AbstractOtpValidatorUTest extends AbstractUTest {

    protected TestOtpData sampleTransactionData() {
        return TestFixtures.otpData(
                TRANSACTION_ID, "acc-123", new BigDecimal("100.00"), "USD");
    }
}
