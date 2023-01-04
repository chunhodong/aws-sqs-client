package com.github.chunhodong.awssqsclient.client;

import com.github.chunhodong.awssqsclient.message.PushMessage;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

import java.util.Objects;

public class AwsSQSClient implements SQSClient<PushMessage> {

    private QueueMessagingTemplate queueMessagingTemplate;

    public AwsSQSClient(QueueMessagingTemplate queueMessagingTemplate) {
        Objects.nonNull(queueMessagingTemplate);
        this.queueMessagingTemplate = queueMessagingTemplate;
    }

    @Override
    public void send(String channel, PushMessage pushMessage) {

    }
}
