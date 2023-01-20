package com.github.chunhodong.awssqsclient.pool;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PoolEntryTest {

    @Test
    @DisplayName("엔트리의 상태를 사용중으로 수정한다")
    void returnUseState() {
        PoolEntry poolEntry = new PoolEntry((channel, message) -> {
        });
        poolEntry.use();

        assertThat(poolEntry.isUse()).isTrue();

    }

    @Test
    @DisplayName("엔트리의 상태를 비사용중으로 수정한다")
    void returnUnuseState() {
        PoolEntry poolEntry = new PoolEntry((channel, message) -> {
        });
        poolEntry.unuse();

        assertThat(poolEntry.isUse()).isFalse();
    }

}
