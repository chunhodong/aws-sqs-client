package com.github.chunhodong.awssqsclient.template;

import com.amazonaws.services.sqs.buffered.AmazonSQSBufferedAsyncClient;
import com.github.chunhodong.awssqsclient.client.AwsSQSClient;
import com.github.chunhodong.awssqsclient.client.SQSClient;
import com.github.chunhodong.awssqsclient.pool.AwsSQSClientPool;
import com.github.chunhodong.awssqsclient.pool.AwsSQSClientPoolImpl;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
public class AwsSQSClientTemplate<T> {

    private static final int DEFAULT_POOL_SIZE = 10;
    private final String channel;
    private final AwsSQSClientPool clientPool;

    private AwsSQSClientTemplate(AwsSQSClientTemplateBuilder builder) {
        validationAttribute(builder);
        this.channel = builder.channel;
        this.clientPool = createPool(builder);
    }

    private AwsSQSClientPool createPool(AwsSQSClientTemplateBuilder builder) {
        List<SQSClient> clients = createClients(builder.poolSize, builder.asyncClient);
        return new AwsSQSClientPoolImpl(clients, builder.asyncClient);
    }

    private List<SQSClient> createClients(int poolSize, AmazonSQSBufferedAsyncClient asyncClient) {
        return Collections.nCopies(poolSize, AwsSQSClient.createClient(asyncClient));
    }

    private void validationAttribute(AwsSQSClientTemplateBuilder builder) {
        builder.poolSize = Objects.requireNonNullElse(builder.poolSize, DEFAULT_POOL_SIZE);
        Objects.requireNonNull(builder.asyncClient);
        Objects.requireNonNull(builder.channel);
    }

    public void send(T message) {
        SQSClient sqsClient = null;
        try {
            sqsClient = clientPool.getClient();
            sqsClient.send(channel, message);
        } finally {
            if (Objects.nonNull(sqsClient)) {
                clientPool.release(sqsClient);
            }
        }
    }

    public static AwsSQSClientTemplateBuilder builder() {
        return new AwsSQSClientTemplateBuilder();
    }

    public static class AwsSQSClientTemplateBuilder {

        private Integer poolSize;
        private String channel;
        private AmazonSQSBufferedAsyncClient asyncClient;

        public AwsSQSClientTemplateBuilder poolSize(int poolSize) {
            this.poolSize = poolSize;
            return this;
        }

        public AwsSQSClientTemplateBuilder channel(String channel) {
            this.channel = channel;
            return this;
        }

        public AwsSQSClientTemplateBuilder asyncClient(AmazonSQSBufferedAsyncClient asyncClient) {
            this.asyncClient = asyncClient;
            return this;
        }

        public AwsSQSClientTemplate build() {
            return new AwsSQSClientTemplate(this);
        }
    }
}
