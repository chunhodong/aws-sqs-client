package com.github.chunhodong.awssqsclient.client;

import com.amazonaws.services.sqs.buffered.AmazonSQSBufferedAsyncClient;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

import java.util.Objects;

public class AwsSQSClient implements SQSClient {

    private QueueMessagingTemplate queueMessagingTemplate;

    public AwsSQSClient(QueueMessagingTemplate queueMessagingTemplate) {
        this.queueMessagingTemplate = queueMessagingTemplate;
    }

    @Override
    public void send(String channel, Object message) {
        validateMessage(channel, message);
        queueMessagingTemplate.convertAndSend(channel, message);
    }

    private void validateMessage(String channel, Object message) {
        Objects.nonNull(channel);
        Objects.nonNull(message);
    }

    public static AwsSQSClient createClient(AmazonSQSBufferedAsyncClient asyncClient) {
        return new AwsSQSClient(new QueueMessagingTemplate(asyncClient));
    }
}
