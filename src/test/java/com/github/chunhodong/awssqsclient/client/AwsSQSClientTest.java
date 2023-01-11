package com.github.chunhodong.awssqsclient.client;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
public class AwsSQSClientTest {
    @InjectMocks
    private AwsSQSClient awsSQSClient;
    @Mock
    private QueueMessagingTemplate queueMessagingTemplate;

    @Test
    @DisplayName("채널과 메시지가 존재하면 메시지를 전송")
    void createClientTemplate() {
        doNothing().when(queueMessagingTemplate).convertAndSend(anyString(), (Object) any());

        awsSQSClient.send("test", "test");
    }
}
