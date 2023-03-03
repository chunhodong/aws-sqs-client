package com.github.chunhodong.awssqsclient.template;

import com.amazonaws.services.sqs.buffered.AmazonSQSBufferedAsyncClient;
import com.github.chunhodong.awssqsclient.client.SQSClient;
import com.github.chunhodong.awssqsclient.pool.AwsSQSClientPool;
import com.github.chunhodong.awssqsclient.pool.AwsSQSClientPoolImpl;
import com.github.chunhodong.awssqsclient.pool.ConnectionTimeoutException;
import com.github.chunhodong.awssqsclient.pool.PoolConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class AwsSQSClientTemplate<T> {
    private final Logger logger = LoggerFactory.getLogger(AwsSQSClientTemplate.class);
    private final String channel;
    private final AwsSQSClientPool clientPool;

    private AwsSQSClientTemplate(AwsSQSClientTemplateBuilder builder) {
        validationAttribute(builder);
        this.channel = builder.channel;
        this.clientPool = createPool(builder);
    }

    private AwsSQSClientPool createPool(AwsSQSClientTemplateBuilder builder) {
        return new AwsSQSClientPoolImpl(builder.poolConfig, builder.asyncClient);
    }

    private void validationAttribute(AwsSQSClientTemplateBuilder builder) {
        builder.poolConfig = Objects.requireNonNullElse(builder.poolConfig, new PoolConfiguration());
        Objects.requireNonNull(builder.asyncClient);
        Objects.requireNonNull(builder.channel);
    }

    public void send(T message) {
        SQSClient sqsClient = null;
        try {
            sqsClient = clientPool.getClient();
            sqsClient.send(channel, message);
        }
        catch (ConnectionTimeoutException exception){
            logger.info("cliient pool unexpected exception - {}",exception.getMessage());
        }
        catch (Exception exception){
            logger.info("cliient pool unexpected exception - {}",exception.getMessage());
        }
        finally {
            if (Objects.nonNull(sqsClient)) {
                clientPool.release(sqsClient);
            }
        }
    }

    public static AwsSQSClientTemplateBuilder builder() {
        return new AwsSQSClientTemplateBuilder();
    }

    public static class AwsSQSClientTemplateBuilder {

        private PoolConfiguration poolConfig;
        private String channel;
        private AmazonSQSBufferedAsyncClient asyncClient;

        public AwsSQSClientTemplateBuilder poolConfig(PoolConfiguration poolConfig) {
            this.poolConfig = poolConfig;
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
