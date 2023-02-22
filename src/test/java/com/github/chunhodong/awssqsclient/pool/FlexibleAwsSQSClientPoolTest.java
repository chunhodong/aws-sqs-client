package com.github.chunhodong.awssqsclient.pool;

import com.amazonaws.services.sqs.buffered.AmazonSQSBufferedAsyncClient;
import com.github.chunhodong.awssqsclient.client.AwsSQSClient;
import com.github.chunhodong.awssqsclient.client.SQSClient;
import com.github.chunhodong.awssqsclient.utils.Timeout;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
    void returnPoolSizeAtMaxPoolSize() throws InterruptedException {
        AmazonSQSBufferedAsyncClient asyncClient = mock(AmazonSQSBufferedAsyncClient.class);
        List<SQSClient> sqsClients = Arrays.asList((channel, message) -> {
        });
        FlexibleAwsSQSClientPool flexibleAwsSQSClientPool = new FlexibleAwsSQSClientPool(100, sqsClients, asyncClient);
        int totalNumberOfTasks = 150;
        ExecutorService executor = Executors.newFixedThreadPool(200);

        CountDownLatch latch = new CountDownLatch(totalNumberOfTasks);
        for (int i = 0; i < totalNumberOfTasks; i++) {
            executor.submit(() -> {
                flexibleAwsSQSClientPool.createEntry();
                latch.countDown();
            });
        }
        latch.await();

        assertThat(flexibleAwsSQSClientPool.getPoolSize()).isEqualTo(100);
    }

    @Test
    @DisplayName("멀티스레드상황에서 삭제횟수와 생성횟수가 같으면 pool개수는 0")
    void returnZeroPoolSize() throws InterruptedException {
        Timeout connectionTime = Timeout.defaultConnectionTime();
        Timeout idleTimeout = new Timeout(TimeUnit.NANOSECONDS, 10l);
        AmazonSQSBufferedAsyncClient asyncClient = mock(AmazonSQSBufferedAsyncClient.class);
        List<SQSClient> sqsClients = new ArrayList<>();
        FlexibleAwsSQSClientPool flexibleAwsSQSClientPool = new FlexibleAwsSQSClientPool(200, connectionTime, idleTimeout, sqsClients, asyncClient);
        int totalNumberOfTasks = 100;


        ExecutorService executor = Executors.newFixedThreadPool(200);

        CountDownLatch latch = new CountDownLatch(totalNumberOfTasks);
        for (int i = 0; i < totalNumberOfTasks; i++) {
            flexibleAwsSQSClientPool.addEntry(new PoolEntry(new AwsSQSClient(new QueueMessagingTemplate(asyncClient)), PoolEntryState.OPEN));
        }

        for (int i = 0; i < totalNumberOfTasks; i++) {
            executor.submit(() -> {
                flexibleAwsSQSClientPool.removeIdleEntry();
                latch.countDown();
            });
        }
        latch.await();

        assertThat(flexibleAwsSQSClientPool.getPoolSize()).isEqualTo(100);
    }

}
