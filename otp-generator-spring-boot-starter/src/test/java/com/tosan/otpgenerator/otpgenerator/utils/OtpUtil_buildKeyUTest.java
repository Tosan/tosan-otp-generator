package com.tosan.otpgenerator.otpgenerator.utils;

import com.tosan.otpgenerator.otpgenerator.AbstractUTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
class OtpUtil_buildKeyUTest extends AbstractUTest {

    @Test
    void validUserIdAndTransactionId_returnsTransactionKey() {

        String result = realOtpUtil.buildTransactionKey(USER_ID, TRANSACTION_ID);
        assertEquals("user-1_tx-1", result);
    }
}
