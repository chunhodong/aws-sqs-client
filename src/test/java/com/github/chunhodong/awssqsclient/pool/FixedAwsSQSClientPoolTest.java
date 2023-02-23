package com.github.chunhodong.awssqsclient.pool;

import com.amazonaws.services.sqs.buffered.AmazonSQSBufferedAsyncClient;
import com.github.chunhodong.awssqsclient.client.SQSClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

public class FixedAwsSQSClientPoolTest {

    @Test
    @DisplayName("Pool 객체를 생성한다")
    void createClientTemplate() {
        AmazonSQSBufferedAsyncClient asyncClient = mock(AmazonSQSBufferedAsyncClient.class);

        List<SQSClient> sqsClients = new ArrayList<>();
        new FixedAwsSQSClientPoolImpl(sqsClients, asyncClient);
    }

    @Test
    @DisplayName("Pool에 추가될 entry가 null이면 생성실패")
    void throwsExceptionWhenEntryIsNull() {
        AmazonSQSBufferedAsyncClient asyncClient = mock(AmazonSQSBufferedAsyncClient.class);

        assertThatThrownBy(() -> new FixedAwsSQSClientPoolImpl(null, asyncClient))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Pool에 추가될 async객체가 null이면 생성실패")
    void throwsExceptionWhenAsyncClientIsNull() {
        List<SQSClient> sqsClients = new ArrayList<>();

        assertThatThrownBy(() -> new FixedAwsSQSClientPoolImpl(sqsClients, null))
                .isInstanceOf(NullPointerException.class);
    }

}
