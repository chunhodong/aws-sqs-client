package com.github.chunhodong.awssqsclient.pool;

import com.amazonaws.services.sqs.buffered.AmazonSQSBufferedAsyncClient;
import com.github.chunhodong.awssqsclient.client.ProxyAwsSQSClient;
import com.github.chunhodong.awssqsclient.client.SQSClient;
import com.github.chunhodong.awssqsclient.utils.Timeout;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class DefaultAwsSQSClientPool implements AwsSQSClientPool {

    private final int poolSize;
    private final List<PoolEntry> entries;
    private final ThreadLocal<LocalDateTime> clientRequestTime;
    private final AmazonSQSBufferedAsyncClient asyncClient;
    private final Timeout connectionTimeout;
    private final Timeout idleTimeout;
    private final Map<ProxyAwsSQSClient, PoolEntry> proxySQSClients;

    public DefaultAwsSQSClientPool(int poolSize,
                                   List<SQSClient> clients,
                                   AmazonSQSBufferedAsyncClient asyncClient,
                                   Timeout connectionTimeout,
                                   Timeout idleTimeout) {
        validateClientPool(clients, asyncClient);
        List<PoolEntry> entries = clients.stream().map(PoolEntry::new).collect(Collectors.toList());
        this.poolSize = poolSize;
        this.entries = Collections.synchronizedList(entries);
        this.asyncClient = asyncClient;
        this.clientRequestTime = new ThreadLocal();
        this.connectionTimeout = Objects.requireNonNullElse(connectionTimeout, Timeout.defaultConnectionTime());
        this.idleTimeout = Objects.requireNonNullElse(idleTimeout, Timeout.defaultIdleTime());
        this.proxySQSClients = Collections.synchronizedMap(new HashMap());
    }

    public DefaultAwsSQSClientPool(int poolSize,
                                   List<SQSClient> clients,
                                   AmazonSQSBufferedAsyncClient asyncClient) {
        this(poolSize, clients, asyncClient, Timeout.defaultConnectionTime(), Timeout.defaultIdleTime());
    }

    private void validateClientPool(List<SQSClient> clients, AmazonSQSBufferedAsyncClient asyncClient) {
        Objects.requireNonNull(clients);
        Objects.requireNonNull(asyncClient);
    }

    @Override
    public SQSClient getClient() {
        PoolEntry entry = getEntry();
        ProxyAwsSQSClient proxyAwsSQSClient = new ProxyAwsSQSClient(entry);
        return proxyAwsSQSClient;
    }

    @Override
    public PoolEntry getEntry() {
        clientRequestTime.set(LocalDateTime.now());
        do {
            for (int i = 0; i < entries.size(); i++) {
                PoolEntry poolEntry = entries.get(i);
                if (!poolEntry.isUse()) {
                    try
                    {
                        poolEntry.accessLock();
                        if (!poolEntry.isUse())
                        {
                            poolEntry.use();
                            return poolEntry;
                        }
                    }
                    finally
                    {
                        clientRequestTime.remove();
                        poolEntry.releaseLock();
                    }
                }
            }
        } while (isTimeout());
        clientRequestTime.remove();
        throw new ClientPoolRequestTimeoutException();
    }

    @Override
    public void release(SQSClient sqsClient) {
        PoolEntry poolEntry = proxySQSClients.get(sqsClient);
        poolEntry.unuse();
    }

    private boolean isTimeout() {
        return !connectionTimeout.isAfter(clientRequestTime.get());
    }
}
