package com.github.chunhodong.awssqsclient.pool;

import com.amazonaws.services.sqs.buffered.AmazonSQSBufferedAsyncClient;
import com.github.chunhodong.awssqsclient.client.SQSClient;
import com.github.chunhodong.awssqsclient.utils.Timeout;
import org.apache.http.pool.PoolEntry;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public abstract class DefaultAwsSQSClientPool implements AwsSQSClientPool {
/*

    private final List<PoolEntry> entries;
    private final ThreadLocal<LocalDateTime> clientRequestTime;
    private final AmazonSQSBufferedAsyncClient asyncClient;
    private final Timeout connectionTimeout;
    private final Timeout idleTimeout;
    private final Map<ProxyAwsSQSClient, PoolEntry> proxySQSClients;

    public DefaultAwsSQSClientPool(List<SQSClient> clients,
                                   AmazonSQSBufferedAsyncClient asyncClient,
                                   Timeout connectionTimeout,
                                   Timeout idleTimeout
    ) {
        validateClientPool(clients, asyncClient, connectionTimeout);
        List<PoolEntry> entries = clients.stream().map(PoolEntry::new).collect(Collectors.toList());
        this.entries = Collections.synchronizedList(entries);
        this.asyncClient = asyncClient;
        this.clientRequestTime = new ThreadLocal();
        this.connectionTimeout = connectionTimeout;
        this.idleTimeout = idleTimeout;
        this.proxySQSClients = Collections.synchronizedMap(new HashMap());
    }

    public DefaultAwsSQSClientPool(List<SQSClient> clients,
                                   AmazonSQSBufferedAsyncClient asyncClient) {
        this(clients, asyncClient, Timeout.defaultConnectionTime(), Timeout.defaultIdleTime());
    }

    private void validateClientPool(List<SQSClient> clients, AmazonSQSBufferedAsyncClient asyncClient, Timeout timeout) {
        Objects.nonNull(clients);
        Objects.nonNull(asyncClient);
        Objects.nonNull(timeout);
    }

    @Override
    public SQSClient getClient() {
        PoolEntry entry = getEntry();
        ProxyAwsSQSClient proxyAwsSQSClient = new ProxyAwsSQSClient(entry);
        this.proxySQSClients.put(proxyAwsSQSClient, entry);
        return proxyAwsSQSClient;
    }

    @Override
    public PoolEntry getEntry() {
        clientRequestTime.set(LocalDateTime.now());
        do {
            for (int i = 0; i < entries.size(); i++) {
                PoolEntry poolEntry = getEntry(i);
                if (Objects.isNull(poolEntry)) break;
                if (!poolEntry.isUse()) {
                    try {
                        poolEntry.accessLock();
                        if (!poolEntry.isUse()) {
                            poolEntry.use();
                            return poolEntry;
                        }
                    } finally {
                        clientRequestTime.remove();
                        poolEntry.releaseLock();
                    }
                }
            }
            PoolEntry poolEntry = addEntry();
            if (Objects.nonNull(poolEntry)) {
                return poolEntry;
            }
        } while (isTimeout());
        clientRequestTime.remove();
        throw new ClientPoolRequestTimeoutException();
    }

    public PoolEntry getEntry(int index) {
        return index < entries.size() ? entries.get(index) : null;
    }

    private boolean isTimeout() {
        return !connectionTimeout.isAfter(clientRequestTime.get());
    }

    public int getPoolSize() {
        return entries.size();
    }

    protected abstract PoolEntry addEntry();

    protected void doAddEntry(PoolEntry poolEntry) {
        entries.add(poolEntry);
    }

    protected PoolEntry newEntry() {
        return new PoolEntry(new AwsSQSClient(new QueueMessagingTemplate(asyncClient)));
    }

    protected void removeIdleEntry(int index) {
        int entrySize = entries.size();
        for (int i = 0; i < entrySize; i++) {
            PoolEntry entry = entries.get(i);
            if (entry.isIdle(idleTimeout)) {
                entries.remove(index);
            }
        }
    }

    @Override
    public void release(SQSClient sqsClient) {
        PoolEntry poolEntry = proxySQSClients.get(sqsClient);
        poolEntry.unuse();
    }
*/

}
