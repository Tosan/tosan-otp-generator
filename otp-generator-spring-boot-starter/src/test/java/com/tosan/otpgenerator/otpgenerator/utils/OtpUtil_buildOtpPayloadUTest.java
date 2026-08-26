package com.tosan.otpgenerator.otpgenerator.utils;

import com.tosan.otpgenerator.otpgenerator.AbstractUTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
class OtpUtil_buildOtpPayloadUTest extends AbstractUTest {

    @Test
    void validTransactionStringAndTimeCounter_returnsExpectedFormat() {

        String result = realOtpUtil.buildOtpPayload("dataIdentifier=tx-1", 42L);
        assertEquals("42|dataIdentifier=tx-1", result);
    }

    @Test
    void zeroTimeCounter_returnsZeroPrefixedPayload() {

        String result = realOtpUtil.buildOtpPayload("dataIdentifier=tx-1", 0L);
        assertEquals("0|dataIdentifier=tx-1", result);
    }
}
