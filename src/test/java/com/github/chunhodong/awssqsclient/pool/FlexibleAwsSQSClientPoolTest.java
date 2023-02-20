package com.github.chunhodong.awssqsclient.pool;

import com.amazonaws.services.sqs.buffered.AmazonSQSBufferedAsyncClient;
import com.github.chunhodong.awssqsclient.client.SQSClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class FlexibleAwsSQSClientPoolTest {

    @Test
    @DisplayName("Pool에있는 entry객체수를 조회")
    void returnFlxiblePoolSize() {
        AmazonSQSBufferedAsyncClient asyncClient = mock(AmazonSQSBufferedAsyncClient.class);
        List<SQSClient> sqsClients = Arrays.asList((channel, message) -> {
        }, (channel, message) -> {
        });

        FlexibleAwsSQSClientPool flexibleAwsSQSClientPool = new FlexibleAwsSQSClientPool(10, sqsClients, asyncClient);

        assertThat(flexibleAwsSQSClientPool.getPoolSize()).isEqualTo(2);
    }

    @Test
    @DisplayName("Pool에있는 entry를 추가하면 poolSize에 반영")
    void returnPoolSizeWhenAddEntry() {
        AmazonSQSBufferedAsyncClient asyncClient = mock(AmazonSQSBufferedAsyncClient.class);
        List<SQSClient> sqsClients = Arrays.asList((channel, message) -> {
        }, (channel, message) -> {
        });

        FlexibleAwsSQSClientPool flexibleAwsSQSClientPool = new FlexibleAwsSQSClientPool(10, sqsClients, asyncClient);
        flexibleAwsSQSClientPool.createEntry();
        assertThat(flexibleAwsSQSClientPool.getPoolSize()).isEqualTo(3);
    }

    @Test
    @DisplayName("멀티스레드상황에서 entry를 추가하면 maxPoolSize개수까지만 생성")
    void returnPoolSizeAtMaxPoolSize() {
    }

}
