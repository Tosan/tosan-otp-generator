package com.tosan.otpgenerator.otpgenerator.utils;

import com.tosan.otpgenerator.otpgenerator.AbstractUTest;
import com.tosan.otpgenerator.otpgenerator.TestFixtures;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
class OtpUtil_buildTransactionDataStringUTest extends AbstractUTest {

    @Test
    void multipleFields_returnsUrlEncodedSortedPairs() {

        var transactionData = TestFixtures.otpData(
                TRANSACTION_ID, "acc-123", new BigDecimal("100.00"), "USD");

        String result = realOtpUtil.buildTransactionDataString(transactionData);

        assertEquals("accountNumber=acc-123&amount=100.00&currency=USD&dataIdentifier=tx-1", result);
    }

    @Test
    void allNullValuesExceptDataIdentifier_returnsOnlyNonNullFields() {

        var transactionData = TestFixtures.otpData(TRANSACTION_ID, null, null, null);

        String result = realOtpUtil.buildTransactionDataString(transactionData);

        assertEquals("dataIdentifier=tx-1", result);
    }

    @Test
    void allNullValues_returnsEmptyString() {

        var transactionData = new TestFixtures.TestOtpData();

        String result = realOtpUtil.buildTransactionDataString(transactionData);

        assertEquals("", result);
    }
}
