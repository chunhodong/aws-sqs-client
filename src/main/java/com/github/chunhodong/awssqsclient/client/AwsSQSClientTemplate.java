package com.github.chunhodong.awssqsclient.client;

import com.amazonaws.services.sqs.buffered.AmazonSQSBufferedAsyncClient;
import com.github.chunhodong.awssqsclient.pool.AwsSQSClientPool;

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
        return null;
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
