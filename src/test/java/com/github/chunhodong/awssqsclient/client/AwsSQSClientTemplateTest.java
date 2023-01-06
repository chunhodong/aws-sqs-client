package com.github.chunhodong.awssqsclient.client;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.buffered.AmazonSQSBufferedAsyncClient;
import com.github.chunhodong.awssqsclient.config.AwsConfig;
import com.github.chunhodong.awssqsclient.template.AwsSQSClientTemplate;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Import({AwsConfig.class})
@SpringBootTest
public class AwsSQSClientTemplateTest {
    @Autowired
    public AmazonSQSBufferedAsyncClient asyncClient;

    @Test
    @Disabled
    @DisplayName("템플릿 객체를 생성한다")
    void createClientTemplate(){
        AwsSQSClientTemplate awsSQSClientTemplate = AwsSQSClientTemplate.builder()
                .asyncClient(new AmazonSQSBufferedAsyncClient(asyncClient))
                .channel("test")
                .build();

        assertThat(awsSQSClientTemplate).isNotNull();
    }

    @Test
    @DisplayName("템플릿 객체에 전송채널이름이 없으면 생성실패")
    void throwsExceptionWhenChanneIsNull(){
        assertThatThrownBy(() -> AwsSQSClientTemplate.builder()
                .asyncClient(new AmazonSQSBufferedAsyncClient(null))
                .build())
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("템플릿 객체에 AmazonSQSBufferedAsyncClient객체가 없으면 예외발생")
    void throwsExceptionWhenAwsSQSClientIsNull(){
        assertThatThrownBy(() -> AwsSQSClientTemplate.builder()
                .channel("test channel")
                .build())
                .isInstanceOf(NullPointerException.class);
    }
}
