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

public class AwsSQSClientPoolImpl implements AwsSQSClientPool {

    protected final Object lock = new Object();
    private final List<PoolElement> elements;
    private final ThreadLocal<LocalDateTime> clientRequestTime;
    private final AmazonSQSBufferedAsyncClient asyncClient;
    private final PoolConfiguration poolConfig;
    private final Map<ProxyAwsSQSClient, PoolElement> proxySQSClients;

    public AwsSQSClientPoolImpl(PoolConfiguration poolConfig,
                                AmazonSQSBufferedAsyncClient asyncClient) {
        this.poolConfig = poolConfig;
        this.elements = createElements(poolConfig,asyncClient);
        this.asyncClient = asyncClient;
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
        PoolElement entry = getEntry();
        ProxyAwsSQSClient proxyAwsSQSClient = new ProxyAwsSQSClient(entry);
        this.proxySQSClients.put(proxyAwsSQSClient, entry);
        return proxyAwsSQSClient;
    }

    @Override
    public PoolElement getEntry() {
        clientRequestTime.set(LocalDateTime.now());
        do {
            for (PoolElement element : elements) {
                if (element.close()) {
                    clientRequestTime.remove();
                    return element;
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

    protected PoolElement publishEntry() {
        return null;
    }

    protected PoolElement newEntry() {
        return new PoolElement(new AwsSQSClient(new QueueMessagingTemplate(asyncClient)));
    }

    protected void addEntry(PoolElement entry) {
        elements.add(entry);
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
