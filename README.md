## AWS SQSClient Pool
[![GitHub license](https://img.shields.io/badge/License-Apache%202.0-green.svg)](https://github.com/chunhodong/property-breaker/blob/master/License)
<br>
[Eng.ver]
<br>
Spring framework provides QueueMessagingTemplate to send messages to Aws Simple Queue.
Internally, it calls sendMessageSync of AmazonSQSBufferedAsyncClient to send the message.
The sendMessageSync method enters the synchronized section with sendMessageLock during the calling process.
This method causes the calling threads to wait, resulting in poor performance.

To improve this problem, I created an Aws Sqs Client Pool that pools QueueMessagingTemplate.
Aws Sqs Client Pool pools and manages QueueMessagingTemplate instances. You can use the pooled QueueMessagingTemplate using the AwsSQSClientTemplate by setting the pooling condition.
If you have any technical questions or problems, please leave them on github issues and we will check and answer them.

[Kor.ver]
<br>
Springframework는 Aws Simple Queue에 메시지를 전송하기위해 QueueMessagingTemplate을 제공합니다. 
내부에서는 메시지를 전송하기위해 AmazonSQSBufferedAsyncClient의 sendMessageSync를 호출합니다.
sendMessageSync메소드는 호출과정에서 sendMessageLock을 가지고 synchronized구분에 진입합니다.
이 방식으로 인해 호출스레드들이 대기를 하게 되고 결과적으로 퍼포먼스가 떨어지게 됩니다.

이 문제를 개선하기 위해 QueueMessagingTemplate을 풀링해두는 Aws Sqs Client Pool을 만들어봤습니다.
Aws Sqs Client Pool은 QueueMessagingTemplate인스턴스들을 풀링해두고 관리합니다. AwsSQSClientTemplateBuilder를 이용하여
풀링 조건을 설정하면 AwsSQSClientTemplate을 사용하여 풀링된 QueueMessagingTemplate를 사용할 수 있습니다.
기술적으로 궁금한점이나 문제점에 대해선 github 이슈에 남겨주시면 확인하고 답변드리겠습니다.


## How to start
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.chunhodong:aws-sqs-client-pool:0.0.9'
}
```

## How to use
```java
@Configuration
public class AwsSQSClientTemplateConfig {
    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;
    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;
    @Value("${cloud.aws.region.static}")
    private String region;
    @Value("${cloud.aws.sqs.queue.url}")
    private String url;
    @Value("${cloud.aws.sqs.queue.name}")
    private String channel;

    @Bean
    public AwsSQSClientTemplate awsSQSClientTemplate() {
        AmazonSQSAsyncClientBuilder builder = AmazonSQSAsyncClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(url, region));
        AmazonSQSBufferedAsyncClient asyncClient = new AmazonSQSBufferedAsyncClient(builder.build());
        
        return AwsSQSClientTemplate.builder()
                .asyncClient(asyncClient)
                .channel(channel)
                .poolConfig(PoolConfiguration.builder()
                        .poolSize(300)
                        .idleTimeout(30000)
                        .connectionTimeout(30000)
                        .build())
                .build();
    }
}

```

```java
@Service
@RequiredArgsConstructor
public class SampleService {

    private final AwsSQSClientTemplate awsSQSClientTemplate;

    public void send(Object message) {
        awsSQSClientTemplate.send(message);
    }
}
```
