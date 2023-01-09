package com.github.chunhodong.awssqsclient.client;

import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

import java.util.Objects;

public class AwsSQSClient implements SQSClient {

    private QueueMessagingTemplate queueMessagingTemplate;

    public AwsSQSClient(QueueMessagingTemplate queueMessagingTemplate) {
        Objects.nonNull(queueMessagingTemplate);
        this.queueMessagingTemplate = queueMessagingTemplate;
    }

    @Override
    public void send(String channel, Object pushMessage) {
        queueMessagingTemplate.convertAndSend(channel, pushMessage);
    }
}
