package com.github.chunhodong.awssqsclient.template;

import com.amazonaws.services.sqs.buffered.AmazonSQSBufferedAsyncClient;
import com.github.chunhodong.awssqsclient.client.AwsSQSClient;
import com.github.chunhodong.awssqsclient.client.SQSClient;
import com.github.chunhodong.awssqsclient.pool.AwsSQSClientPool;
import com.github.chunhodong.awssqsclient.pool.FixedAwsSQSClientPoolImpl;
import com.github.chunhodong.awssqsclient.pool.FlexibleAwsSQSClientPoolImpl;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
public class AwsSQSClientTemplate<T> {

    private static final int DEFAULT_MAX_POOL_SIZE = 100;
    private static final int DEFAULT_MIN_POOL_SIZE = 1;
    private final String channel;
    private final AwsSQSClientPool clientPool;

    private AwsSQSClientTemplate(AwsSQSClientTemplateBuilder builder) {
        validationAttribute(builder);
        this.channel = builder.channel;
        this.clientPool = createPool(builder);
    }

    private AwsSQSClientPool createPool(AwsSQSClientTemplateBuilder builder) {
        int poolSize = getPoolSize(builder.isFixedPoolsize, builder.poolSize);
        List<SQSClient> clients = createClients(builder.isFixedPoolsize, poolSize, builder.asyncClient);
        return builder.isFixedPoolsize
                ? new FixedAwsSQSClientPoolImpl(clients, builder.asyncClient)
                : new FlexibleAwsSQSClientPoolImpl(poolSize, clients, builder.asyncClient);
    }

    private List<SQSClient> createClients(boolean isFixedPoolsize, int maxPoolSize, AmazonSQSBufferedAsyncClient asyncClient) {
        int poolSize = isFixedPoolsize ? maxPoolSize : DEFAULT_MIN_POOL_SIZE;
        return Collections.nCopies(poolSize, AwsSQSClient.createClient(asyncClient));
    }

    private int getPoolSize(boolean isFixedPoolsize, Integer maxPoolSize) {
        return !isFixedPoolsize && Objects.isNull(maxPoolSize) ? DEFAULT_MAX_POOL_SIZE : maxPoolSize;
    }

    private void validationAttribute(AwsSQSClientTemplateBuilder builder) {
        Objects.requireNonNull(builder.asyncClient);
        Objects.requireNonNull(builder.channel);
        if (builder.isFixedPoolsize) {
            Objects.requireNonNull(builder.poolSize);
        }
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
        private boolean isFixedPoolsize;

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

        public AwsSQSClientTemplateBuilder isFixedPoolsize(boolean isFixedPoolsize) {
            this.isFixedPoolsize = isFixedPoolsize;
            return this;
        }

        public AwsSQSClientTemplate build() {
            return new AwsSQSClientTemplate(this);
        }
    }
}
