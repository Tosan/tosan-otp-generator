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

        String result = realOtpUtil.buildOtpPayload("dataIdentifier=tx-1", 42L, 1L);
        assertEquals("42|dataIdentifier=tx-1", result);
    }

    @Test
    void issuanceGreaterThanOne_appendsSequenceToPayload() {

        String result = realOtpUtil.buildOtpPayload("dataIdentifier=tx-1", 42L, 2L);
        assertEquals("42|dataIdentifier=tx-1|2", result);
    }
}
