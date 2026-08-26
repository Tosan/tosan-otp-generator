package com.tosan.otpgenerator.utils;

import java.time.Clock;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
public class TimeStepUtil {

    private final Clock clock;

    public TimeStepUtil(Clock clock) {
        this.clock = clock;
    }

    public long currentTimeStep(long stepSeconds) {
        long epochSeconds = clock.millis() / 1000L;
        return epochSeconds / stepSeconds;
    }
}
