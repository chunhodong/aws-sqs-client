package com.github.chunhodong.awssqsclient.pool;

import com.amazonaws.services.sqs.buffered.AmazonSQSBufferedAsyncClient;
import com.github.chunhodong.awssqsclient.client.SQSClient;
import com.github.chunhodong.awssqsclient.utils.Timeout;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DefaultAwsSQSClientPool implements AwsSQSClientPool {

    private final int poolSize;
    private final List<PoolEntry> entries;
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
        validateClientPool(clients, asyncClient, connectionTimeout);
        List<PoolEntry> entries = clients.stream().map(PoolEntry::new).collect(Collectors.toList());
        this.poolSize = poolSize;
        this.entries = Collections.synchronizedList(entries);
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

    private void validateClientPool(List<SQSClient> clients, AmazonSQSBufferedAsyncClient asyncClient, Timeout timeout) {
        Objects.nonNull(clients);
        Objects.nonNull(asyncClient);
        Objects.nonNull(timeout);
    }

    @Override
    public SQSClient getClient() {
        return null;
    }
}
