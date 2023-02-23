## AWS SQSClient Pool
[![GitHub license](https://img.shields.io/badge/License-Apache%202.0-green.svg)](https://github.com/chunhodong/property-breaker/blob/master/License)
<br>
[Eng.ver]
<br>
The SQSClient Pool is that pools QueueMessagingTemplate clients.
The QueueMessagingTemplate library uses the Apache http library to send messages to SQS.
Specifically, the QueueMessagingTemplate library uses Apache's PoolingHttpClientConnectionManager internally for http connections to AWS SQS.

However, QueueMessagingTemplate fixed the maxPerRoute value to 2 among the properties of Apache's PoolingHttpClientConnectionManager.
This isPrevents QueueMessagingTemplate from performing at its best.

The SQSClient Pool creates several QueueMessagingTemplates using pooling. Additionally, Idle QueueMessagingTemplate is periodically removed from the pool to prevent resource issues.
If you have questions or technical problems, please leave them on github issues and we will check and answer them.

[Kor.ver]
<br>
The SQSClient Pool라이브러리는 QueueMessagingTemplate의 문제점을 발견하고 개선하기위해 만든 풀링라이브러리입니다.QueueMessagingTemplate은 Aws SQS에 메시지를 보낼수있는 클라이언트 라이브러리입니다.
내부적으로는 Apache http관련 라이브러리를써서 http요청을 통해 Aws SQS에 메시지를 전송합니다.<br><br>
 그런데 QueueMessagingTemplate는 apache http라이브러리의 필드속성을 고정시켜서 튜닝을 할 수 없게 만들어놨습니다.
가장 큰 단점은 Apache http라이브러리에서 conneciton을 풀링하는데 사용하고있는 PoolingHttpClientConnectionManager의 maxPerRoute속성을 2로 고정시켜놓은 문제입니다.
이로 인해 AWS SQS당 동시접근할수있는 connection은 2개로 제한이 됩니다. 어플리케이션상에서 스레드를 늘려도 maxPerRoute개수가 고정되있기때문에 처리량은 크게 변하지 않습니다. 
<br><br>이 문제를 개선하기위해
자체적인 풀링 라이브러리를 구현했습니다. 풀에 들어갈 요소는 QueueMessagingTemplate으로 QueueMessagingTemplate개수를 관리해서 AWS SQS요청 처리량을 늘리려는 목적입니다. 기술적으로 궁금한점이나 문제점에 대해선
Issue남겨주시면 읽어보고 답변해보겠습니다

## How to start
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.chunhodong:aws-sqs-client-pool:0.0.7'
}
```

## How to use
```yaml

```

```yaml


```

## Effect of use
