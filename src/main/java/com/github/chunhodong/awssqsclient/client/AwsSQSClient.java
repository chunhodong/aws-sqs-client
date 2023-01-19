package com.github.chunhodong.awssqsclient.client;

import com.amazonaws.services.sqs.buffered.AmazonSQSBufferedAsyncClient;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class AwsSQSClient implements SQSClient {

    private final QueueMessagingTemplate queueMessagingTemplate;

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
