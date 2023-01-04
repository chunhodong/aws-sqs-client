package com.github.chunhodong.awssqsclient.template;

import com.amazonaws.services.sqs.buffered.AmazonSQSBufferedAsyncClient;
import com.github.chunhodong.awssqsclient.client.AwsSQSClient;
import com.github.chunhodong.awssqsclient.client.SQSClient;
import com.github.chunhodong.awssqsclient.pool.AwsSQSClientPool;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class AwsSQSClientTemplate {

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
        int maxPoolSize = getPoolSize(builder.isFixedPoolsize, builder.poolSize);
        List<SQSClient> clients = createClients(builder.isFixedPoolsize, maxPoolSize, builder.asyncClient);
        return new AwsSQSClientPool() {
            @Override
            public int hashCode() {
                return super.hashCode();
            }
        };
    }

    private List<SQSClient> createClients(boolean isFixedPoolsize, int maxPoolSize, AmazonSQSBufferedAsyncClient asyncClient) {
        return isFixedPoolsize
                ? Collections.nCopies(maxPoolSize, new AwsSQSClient(new QueueMessagingTemplate(asyncClient)))
                : Collections.nCopies(DEFAULT_MIN_POOL_SIZE, new AwsSQSClient(new QueueMessagingTemplate(asyncClient)));
    }

    private int getPoolSize(boolean isFixedPoolsize, int maxPoolSize) {
        return !isFixedPoolsize && Objects.isNull(maxPoolSize) ? DEFAULT_MAX_POOL_SIZE : maxPoolSize;
    }


    private void validationAttribute(AwsSQSClientTemplateBuilder builder) {
        Objects.requireNonNull(builder);
        Objects.requireNonNull(builder.asyncClient);
        Objects.requireNonNull(builder.channel);
        if (builder.isFixedPoolsize) {
            Objects.requireNonNull(builder.poolSize);
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
