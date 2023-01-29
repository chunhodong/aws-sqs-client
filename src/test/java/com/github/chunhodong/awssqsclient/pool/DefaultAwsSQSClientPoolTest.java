package com.github.chunhodong.awssqsclient.pool;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.buffered.AmazonSQSBufferedAsyncClient;
import com.github.chunhodong.awssqsclient.client.SQSClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DefaultAwsSQSClientPoolTest {

    @Test
    @DisplayName("Pool 객체를 생성한다")
    void createClientTemplate() {
        AmazonSQSAsyncClientBuilder builder = AmazonSQSAsyncClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("accessKey", "secretKey")));
        AmazonSQSBufferedAsyncClient asyncClient = new AmazonSQSBufferedAsyncClient(builder.build());
        List<SQSClient> sqsClients = new ArrayList<>();
        new FixedAwsSQSClientPool(sqsClients, asyncClient);
    }

    @Test
    @DisplayName("Pool에 추가될 entry가 null이면 생성실패")
    void throwsExceptionWhenEntryIsNull() {
        AmazonSQSAsyncClientBuilder builder = AmazonSQSAsyncClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("accessKey", "secretKey")));
        AmazonSQSBufferedAsyncClient asyncClient = new AmazonSQSBufferedAsyncClient(builder.build());
        assertThatThrownBy(() -> new FixedAwsSQSClientPool(null, asyncClient))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Pool에 추가될 async객체가 null이면 생성실패")
    void throwsExceptionWhenAsyncClientIsNull() {
        List<SQSClient> sqsClients = new ArrayList<>();

        assertThatThrownBy(() -> new FixedAwsSQSClientPool(sqsClients, null))
                .isInstanceOf(NullPointerException.class);
    }

}
