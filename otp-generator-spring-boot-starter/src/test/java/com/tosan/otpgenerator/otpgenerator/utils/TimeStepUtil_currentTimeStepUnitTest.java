package com.tosan.otpgenerator.otpgenerator.utils;

import com.tosan.otpgenerator.otpgenerator.AbstractUTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TimeStepUtil_currentTimeStepUnitTest extends AbstractUTest {

    @Test
    void thirtySecondInterval_returnsEpochSecondsDividedByThirty() {

        long result = timeStepUtil.currentTimeStep(30L);
        assertEquals(FIXED_INSTANT.getEpochSecond() / 30L, result);
    }
}
