package com.github.chunhodong.awssqsclient.pool;

import com.amazonaws.services.sqs.buffered.AmazonSQSBufferedAsyncClient;
import com.github.chunhodong.awssqsclient.client.AwsSQSClient;
import com.github.chunhodong.awssqsclient.client.ProxyAwsSQSClient;
import com.github.chunhodong.awssqsclient.client.SQSClient;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AwsSQSClientPoolImpl implements AwsSQSClientPool {

    protected final Object lock = new Object();
    private final List<PoolElement> elements;
    private final ThreadLocal<LocalDateTime> clientRequestTime;
    private final PoolConfiguration poolConfig;
    private final Map<ProxyAwsSQSClient, PoolElement> proxySQSClients;

    public AwsSQSClientPoolImpl(PoolConfiguration poolConfig,
                                AmazonSQSBufferedAsyncClient asyncClient) {
        this.poolConfig = poolConfig;
        this.elements = createElements(poolConfig, asyncClient);
        this.clientRequestTime = new ThreadLocal<>();
        this.proxySQSClients = Collections.synchronizedMap(new HashMap<>());

    }

    private List<PoolElement> createElements(PoolConfiguration poolConfig, AmazonSQSBufferedAsyncClient asyncClient) {
        return Collections.nCopies(poolConfig.getPoolSize(), AwsSQSClient.createClient(asyncClient))
                .stream()
                .map(PoolElement::new)
                .collect(Collectors.toList());
    }

    @Override
    public SQSClient getClient() {
        PoolElement entry = getElement();
        ProxyAwsSQSClient proxyAwsSQSClient = new ProxyAwsSQSClient(entry);
        this.proxySQSClients.put(proxyAwsSQSClient, entry);
        return proxyAwsSQSClient;
    }

    @Override
    public PoolElement getElement() {
        clientRequestTime.set(LocalDateTime.now());
        do {
            for (PoolElement element : elements) {
                if (element.close()) {
                    clientRequestTime.remove();
                    return element;
                }
            }
        } while (isTimeout());
        clientRequestTime.remove();
        throw new ConnectionWaitTimeout();
    }

    @Override
    public void release(SQSClient sqsClient) {
        PoolElement poolElement = proxySQSClients.remove(sqsClient);
        poolElement.open();
    }

    protected int getPoolSize() {
        return elements.size();
    }

    private boolean isTimeout() {
        return !poolConfig.isConnectionTimeout(clientRequestTime.get());
    }

    protected void removeIdleEntry() {
        List<PoolElement> idleEntrys = elements
                .stream()
                .filter(poolEntry -> poolEntry.isIdle(poolConfig.getIdleTimeout()))
                .collect(Collectors.toList());
        synchronized (lock) {
            elements.removeAll(idleEntrys);
        }
    }
}
