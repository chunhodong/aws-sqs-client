package com.github.chunhodong.awssqsclient.pool;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PoolConfigurationTest {

    @Test
    @DisplayName("idletimeout값이 0보다 작으면 예외발생")
    void throwsExceptionWhenIdleTimeoutLessThan0() {
        assertThatThrownBy(() -> PoolConfiguration.builder()
                .idleTimeout(-1)
                .build())
                .isInstanceOf(PoolException.class);
    }

    @Test
    @DisplayName("idletimeout값이 0보다 크고 최소값보다 작으면 예외발생")
    void throwsExceptionWhenIdleTimeoutLessThanMin() {
        assertThatThrownBy(() -> PoolConfiguration.builder()
                .idleTimeout(9)
                .build())
                .isInstanceOf(PoolException.class);
    }

    @Test
    @DisplayName("poolSize가 10보다 작으면 예외발생")
    void throwsExceptionWhenPoolSizeLessThan10() {
        assertThatThrownBy(() -> PoolConfiguration.builder()
                .poolSize(5)
                .build())
                .isInstanceOf(PoolException.class);
    }

    @Test
    @DisplayName("connectionTimeout값이 10000보다 작으면 예외발생")
    void throwsExceptionWhenContimeoutLessThan10000() {
        assertThatThrownBy(() -> PoolConfiguration.builder()
                .connectionTimeout(5000)
                .build())
                .isInstanceOf(PoolException.class);
    }
}
