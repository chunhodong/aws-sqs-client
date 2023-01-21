package com.github.chunhodong.awssqsclient.utils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Timeout {

    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.SECONDS;
    private static final Long DEFAULT_CONNECTION_TIME_VALUE = Long.valueOf(60);
    private static final Long DEFAULT_IDLE_TIME_VALUE = Long.valueOf(60);
    private TimeUnit timeUnit = DEFAULT_TIME_UNIT;
    private Long timeValue;

    private Timeout(TimeUnit timeUnit, Long timeValue) {
        validateTime(timeUnit, timeValue);
        this.timeUnit = timeUnit;
        this.timeValue = timeValue;
    }

    private void validateTime(TimeUnit timeUnit, Long timeValue) {
        Objects.nonNull(timeUnit);
        Objects.nonNull(timeValue);
    }

    public static Timeout defaultConnectionTime() {
        return new Timeout(DEFAULT_TIME_UNIT, DEFAULT_CONNECTION_TIME_VALUE);
    }

    public static Timeout defaultIdleTime() {
        return new Timeout(DEFAULT_TIME_UNIT, DEFAULT_IDLE_TIME_VALUE);
    }

    public boolean isAfter(LocalDateTime dateTime) {
        return Objects.isNull(dateTime) ? false : ChronoUnit.SECONDS.between(dateTime, LocalDateTime.now()) > timeValue;
    }

    public long toMilis() {
        if (timeUnit == TimeUnit.SECONDS) {
            return TimeUnit.SECONDS.toMillis(timeValue);
        }
        return timeValue;
    }
}
