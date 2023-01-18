package com.github.chunhodong.awssqsclient.template;

import com.amazonaws.services.sqs.buffered.AmazonSQSBufferedAsyncClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class AwsSQSClientTemplateTest {


    @Test
    @Disabled
    @DisplayName("템플릿 객체를 생성한다")
    void createClientTemplate() {
        AwsSQSClientTemplate awsSQSClientTemplate = AwsSQSClientTemplate.builder()
                .asyncClient(new AmazonSQSBufferedAsyncClient(null))
                .channel("test")
                .build();

        assertThat(awsSQSClientTemplate).isNotNull();
    }

    @Test
    @DisplayName("템플릿 객체에 전송채널이름이 없으면 생성실패")
    void throwsExceptionWhenChanneIsNull() {
        assertThatThrownBy(() -> AwsSQSClientTemplate.builder()
                .asyncClient(new AmazonSQSBufferedAsyncClient(null))
                .build())
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("템플릿 객체에 AmazonSQSBufferedAsyncClient객체가 없으면 예외발생")
    void throwsExceptionWhenAwsSQSClientIsNull() {
        assertThatThrownBy(() -> AwsSQSClientTemplate.builder()
                .channel("test channel")
                .build())
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("템플릿 객체에 고정사이트풀인데 poolSize가 없으면 생성실패")
    void throwsExceptionWhenNonePoolSizeIfFixedPool() {
        assertThatThrownBy(() -> AwsSQSClientTemplate.builder()
                .asyncClient(new AmazonSQSBufferedAsyncClient(null))
                .channel("test channel")
                .isFixedPoolsize(true)
                .build())
                .isInstanceOf(NullPointerException.class);
    }
}
