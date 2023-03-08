package com.github.chunhodong.awssqsclient.pool;

import com.amazonaws.services.sqs.buffered.AmazonSQSBufferedAsyncClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AwsSQSClientPoolImplTest {

    @Test
    @DisplayName("엔트리를 가져오지 못하면 타임아웃예외발생")
    void returnUnuseState() throws Exception {
        PoolConfiguration poolConfiguration = PoolConfiguration.builder().poolSize(10).build();
        Field field = poolConfiguration.getClass().getDeclaredField("connectionTimeout");
        field.setAccessible(true);
        field.setLong(poolConfiguration, 3000l);
        AmazonSQSBufferedAsyncClient asyncClient = new AmazonSQSBufferedAsyncClient(null);
        AwsSQSClientPoolImpl awsSQSClientPool = new AwsSQSClientPoolImpl(poolConfiguration, asyncClient);

        for (int i = 0; i < 10; i++) {
            awsSQSClientPool.getClient();
        }

        assertThatThrownBy(() -> awsSQSClientPool.getClient()).isInstanceOf(ConnectionTimeoutException.class);
    }

}
