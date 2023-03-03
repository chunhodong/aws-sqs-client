package com.github.chunhodong.awssqsclient.client;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AwsSQSClientTest {
    @Test
    @DisplayName("채널과 메시지가 존재하면 메시지를 전송")
    void createClientTemplate() {
        QueueMessagingTemplate queueMessagingTemplate = mock(QueueMessagingTemplate.class);

        AwsSQSClient awsSQSClient = new AwsSQSClient(queueMessagingTemplate);
        doNothing().when(queueMessagingTemplate).convertAndSend(anyString(), (Object) any());

        awsSQSClient.send("test", "test");
    }

    @Test
    @DisplayName("채널이 null값이면 예외발생")
    void throwsExceptionWhenChannelIsNull() {
        AmazonSQSAsync amazonSQSAsync = spy(AmazonSQSAsync.class);
        AwsSQSClient awsSQSClient = new AwsSQSClient(new QueueMessagingTemplate(amazonSQSAsync));

        assertThatThrownBy(() -> awsSQSClient.send(null, "test"))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("메시지가 null값이면 예외발생")
    void throwsExceptionWhenMessageIsNull() {
        AmazonSQSAsync amazonSQSAsync = spy(AmazonSQSAsync.class);
        AwsSQSClient awsSQSClient = new AwsSQSClient(new QueueMessagingTemplate(amazonSQSAsync));

        assertThatThrownBy(() -> awsSQSClient.send("channel", null))
                .isInstanceOf(NullPointerException.class);
    }
}
