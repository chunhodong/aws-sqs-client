package com.github.chunhodong.awssqsclient.pool;

import com.amazonaws.services.sqs.buffered.AmazonSQSBufferedAsyncClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class AwsSQSClientPoolImplTest {

    @Test
    @DisplayName("Pool에있는 entry객체수를 조회")
    void returnFlxiblePoolSize() {
        AmazonSQSBufferedAsyncClient asyncClient = mock(AmazonSQSBufferedAsyncClient.class);

        AwsSQSClientPoolImpl flexibleAwsSQSClientPool = new AwsSQSClientPoolImpl(new PoolConfiguration(), asyncClient);

        assertThat(flexibleAwsSQSClientPool.getPoolSize()).isEqualTo(2);
    }

}
