package com.github.chunhodong.awssqsclient.pool;

import com.github.chunhodong.awssqsclient.utils.Timeout;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class PoolEntryTest {

    @Test
    @DisplayName("엔트리의 상태를 사용중으로 수정한다")
    void returnUseState() {
        PoolEntry poolEntry = new PoolEntry((channel, message) -> {
        });
        poolEntry.close();

        assertThat(poolEntry.isClose()).isTrue();

    }

    @Test
    @DisplayName("엔트리의 상태를 비사용중으로 수정한다")
    void returnUnuseState() {
        PoolEntry poolEntry = new PoolEntry((channel, message) -> {
        });
        poolEntry.open();

        assertThat(poolEntry.isClose()).isFalse();
    }

    @Test
    @DisplayName("풀엔트리가 오픈상태이면서 생성시간이 입력값보다 많으면 true")
    void returnTrueWhenPoolIsIdle() throws InterruptedException {
        PoolEntry poolEntry = new PoolEntry((channel, message) -> {
        });

        poolEntry.open();
        Thread.sleep(1000);

        assertThat(poolEntry.isIdle(new Timeout(TimeUnit.NANOSECONDS, 100l))).isTrue();

    }

}
