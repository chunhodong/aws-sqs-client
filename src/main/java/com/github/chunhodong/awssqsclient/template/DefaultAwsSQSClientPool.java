package com.github.chunhodong.awssqsclient.template;

import com.amazonaws.services.sqs.buffered.AmazonSQSBufferedAsyncClient;
import com.github.chunhodong.awssqsclient.client.SQSClient;
import com.github.chunhodong.awssqsclient.pool.AwsSQSClientPool;
import com.github.chunhodong.awssqsclient.utils.Timeout;

import java.time.LocalDateTime;
import java.util.List;

public class DefaultAwsSQSClientPool implements AwsSQSClientPool {

    private final ThreadLocal<LocalDateTime> clientRequestTime;
    private final AmazonSQSBufferedAsyncClient asyncClient;
    private final Timeout connectionTimeout;
    private final Timeout idleTimeout;

    public DefaultAwsSQSClientPool(int poolSize,
                                   List<SQSClient> clients,
                                   AmazonSQSBufferedAsyncClient asyncClient,
                                   Timeout connectionTimeout,
                                   Timeout idleTimeout
    ) {
        this.asyncClient = asyncClient;
        this.clientRequestTime = new ThreadLocal();
        this.connectionTimeout = connectionTimeout;
        this.idleTimeout = idleTimeout;
    }

    public DefaultAwsSQSClientPool(int poolSize,
                                   List<SQSClient> clients,
                                   AmazonSQSBufferedAsyncClient asyncClient) {
        this(poolSize, clients, asyncClient, Timeout.defaultConnectionTime(), Timeout.defaultIdleTime());
    }


    @Override
    public SQSClient getClient() {
        return null;
    }
}
