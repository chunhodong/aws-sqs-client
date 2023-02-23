package com.github.chunhodong.awssqsclient.pool;

import com.amazonaws.services.sqs.buffered.AmazonSQSBufferedAsyncClient;
import com.github.chunhodong.awssqsclient.client.AwsSQSClient;
import com.github.chunhodong.awssqsclient.client.ProxyAwsSQSClient;
import com.github.chunhodong.awssqsclient.client.SQSClient;
import com.github.chunhodong.awssqsclient.utils.Timeout;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public abstract class DefaultAwsSQSClientPool implements AwsSQSClientPool {

    protected final Object lock = new Object();
    private final List<PoolElement> entries;
    private final ThreadLocal<LocalDateTime> clientRequestTime;
    private final AmazonSQSBufferedAsyncClient asyncClient;
    private final Timeout connectionTimeout;
    private final Timeout idleTimeout;
    private final Map<ProxyAwsSQSClient, PoolElement> proxySQSClients;

    public DefaultAwsSQSClientPool(List<SQSClient> clients,
                                   AmazonSQSBufferedAsyncClient asyncClient,
                                   Timeout connectionTimeout,
                                   Timeout idleTimeout) {
        validateClientPool(clients, asyncClient);
        this.entries = clients.stream().map(PoolElement::new).collect(Collectors.toList());
        this.asyncClient = asyncClient;
        this.clientRequestTime = new ThreadLocal<>();
        this.connectionTimeout = Objects.requireNonNullElse(connectionTimeout, Timeout.defaultConnectionTime());
        this.idleTimeout = Objects.requireNonNullElse(idleTimeout, Timeout.defaultIdleTime());
        this.proxySQSClients = Collections.synchronizedMap(new HashMap<>());
    }

    public DefaultAwsSQSClientPool(List<SQSClient> clients,
                                   AmazonSQSBufferedAsyncClient asyncClient) {
        this(clients, asyncClient, Timeout.defaultConnectionTime(), Timeout.defaultIdleTime());
    }

    private void validateClientPool(List<SQSClient> clients, AmazonSQSBufferedAsyncClient asyncClient) {
        Objects.requireNonNull(clients);
        Objects.requireNonNull(asyncClient);
    }

    @Override
    public SQSClient getClient() {
        PoolElement entry = getEntry();
        ProxyAwsSQSClient proxyAwsSQSClient = new ProxyAwsSQSClient(entry);
        this.proxySQSClients.put(proxyAwsSQSClient, entry);
        return proxyAwsSQSClient;
    }

    @Override
    public PoolElement getEntry() {
        clientRequestTime.set(LocalDateTime.now());
        do {
            for (PoolElement entry : entries) {
                if (entry.close()) {
                    clientRequestTime.remove();
                    return entry;
                }
            }
            PoolElement poolElement = publishEntry();
            if (Objects.nonNull(poolElement)) {
                return poolElement;
            }
        } while (isTimeout());
        clientRequestTime.remove();
        throw new ClientPoolRequestTimeoutException();
    }

    @Override
    public void release(SQSClient sqsClient) {
        PoolElement poolElement = proxySQSClients.remove(sqsClient);
        poolElement.open();
    }

    protected abstract PoolElement publishEntry();

    protected PoolElement newEntry() {
        return new PoolElement(new AwsSQSClient(new QueueMessagingTemplate(asyncClient)));
    }

    protected void addEntry(PoolElement entry) {
        entries.add(entry);
    }

    protected int getPoolSize() {
        return entries.size();
    }

    private boolean isTimeout() {
        return !connectionTimeout.isAfter(clientRequestTime.get());
    }

    protected void removeIdleEntry() {
        List<PoolElement> idleEntrys = entries
                .stream()
                .filter(poolEntry -> poolEntry.isIdle(idleTimeout))
                .collect(Collectors.toList());
        synchronized (lock) {
            entries.removeAll(idleEntrys);
        }
    }
}
