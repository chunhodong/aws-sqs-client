package com.github.chunhodong.awssqsclient.pool;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PoolElementTest {

    @Test
    @DisplayName("엔트리의 상태를 사용중으로 수정한다")
    void returnUseState() {
        PoolElement poolElement = new PoolElement((channel, message) -> {
        });

        assertThat(poolElement.close()).isTrue();

    }

    @Test
    @DisplayName("엔트리의 상태를 비사용중으로 수정한다")
    void returnUnuseState() {
        PoolElement poolElement = new PoolElement((channel, message) -> {
        });
        poolElement.close();
        assertThat(poolElement.close()).isFalse();
    }

    @Test
    @DisplayName("풀엔트리가 오픈상태이면서 생성시간이 입력값보다 많으면 true")
    void returnTrueWhenPoolIsIdle() throws InterruptedException {
        PoolElement poolElement = new PoolElement((channel, message) -> {
        });

        poolElement.open();
        Thread.sleep(1000);

        assertThat(poolElement.isIdle(10)).isTrue();

    }
}
