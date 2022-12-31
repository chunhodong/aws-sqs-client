package com.github.chunhodong.awssqsclient.client;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class AwsSQSClientTemplateTest {

    @Test
    @DisplayName("템플릿 객체를 생성한다")
    void createClientTemplate(){
        AwsSQSClientTemplate.builder().build();

    }
}
