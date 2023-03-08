package com.github.chunhodong.awssqsclient.pool;

import com.amazonaws.services.sqs.buffered.AmazonSQSBufferedAsyncClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Test
    @DisplayName("풀사이즈가 꽉 찾을때 엔트리생성을하면 null값반환")
    void returnNullWhenPoolSizeIsMax() throws Exception {
        PoolConfiguration poolConfiguration = PoolConfiguration.builder().poolSize(100).build();
        AmazonSQSBufferedAsyncClient asyncClient = new AmazonSQSBufferedAsyncClient(null);
        AwsSQSClientPoolImpl awsSQSClientPool = new AwsSQSClientPoolImpl(poolConfiguration, asyncClient);
        Class clazz = awsSQSClientPool.getClass();
        Method method = clazz.getDeclaredMethod("newElement", AmazonSQSBufferedAsyncClient.class);
        method.setAccessible(true);

        PoolElement poolElement = (PoolElement) method.invoke(awsSQSClientPool,asyncClient);

        assertThat(poolElement).isNull();
    }

    @Test
    @DisplayName("풀사이즈만큼 엔트리가 없을때 엔트리생성을하면 엔트리반환")
    void returnElementWhenLessThanPoolSize() throws Exception {
        PoolConfiguration poolConfiguration = PoolConfiguration.builder().poolSize(100).build();
        Field field = poolConfiguration.getClass().getDeclaredField("idleTimeout");
        field.setAccessible(true);
        field.setLong(poolConfiguration, 100l);
        AmazonSQSBufferedAsyncClient asyncClient = new AmazonSQSBufferedAsyncClient(null);
        AwsSQSClientPoolImpl awsSQSClientPool = new AwsSQSClientPoolImpl(poolConfiguration, asyncClient);
        Class clazz = awsSQSClientPool.getClass();
        Method method = clazz.getDeclaredMethod("newElement", AmazonSQSBufferedAsyncClient.class);
        method.setAccessible(true);
        Thread.sleep(2000);

        PoolElement poolElement = (PoolElement) method.invoke(awsSQSClientPool,asyncClient);

        assertThat(poolElement).isNotNull();
    }

}
