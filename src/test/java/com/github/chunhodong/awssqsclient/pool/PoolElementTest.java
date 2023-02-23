package com.github.chunhodong.awssqsclient.pool;

import com.github.chunhodong.awssqsclient.utils.Timeout;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class PoolElementTest {

    @Test
    @DisplayName("엔트리의 상태를 사용중으로 수정한다")
    void returnUseState() {
        PoolElement poolElement = new PoolElement((channel, message) -> {
        });
        poolElement.close();

        assertThat(poolElement.isClose()).isTrue();

    }

    @Test
    @DisplayName("엔트리의 상태를 비사용중으로 수정한다")
    void returnUnuseState() {
        PoolElement poolElement = new PoolElement((channel, message) -> {
        });
        poolElement.open();

        assertThat(poolElement.isClose()).isFalse();
    }

    @Test
    @DisplayName("풀엔트리가 오픈상태이면서 생성시간이 입력값보다 많으면 true")
    void returnTrueWhenPoolIsIdle() throws InterruptedException {
        PoolElement poolElement = new PoolElement((channel, message) -> {
        });

        poolElement.open();
        Thread.sleep(1000);

        assertThat(poolElement.isIdle(new Timeout(TimeUnit.NANOSECONDS, 100l))).isTrue();

    }

}
