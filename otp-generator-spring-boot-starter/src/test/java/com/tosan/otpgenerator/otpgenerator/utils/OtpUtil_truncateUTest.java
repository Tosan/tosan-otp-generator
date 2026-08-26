package com.tosan.otpgenerator.otpgenerator.utils;

import com.tosan.otpgenerator.exception.OtpException;
import com.tosan.otpgenerator.otpgenerator.AbstractUTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OtpUtil_truncateUTest extends AbstractUTest {

    @Test
    void validTwentyByteHash_returnsSixDigitZeroPaddedString() {

        byte[] hash = new byte[20];
        hash[19] = 0x0F;
        hash[15] = 0x01;
        hash[16] = 0x02;
        hash[17] = 0x03;
        hash[18] = 0x04;

        String result = realOtpUtil.truncate(hash, 6);

        assertEquals(6, result.length());
        assertTrue(result.matches("\\d{6}"));
    }

    @Test
    void hashShorterThanTwentyBytes_throwsOtpExceptionWithTooShortMessage() {

        byte[] hash = new byte[19];
        OtpException exception = assertThrows(OtpException.class, () -> realOtpUtil.truncate(hash, 6));
        assertEquals("HMAC output is too short for dynamic truncation", exception.getMessage());
    }
}
