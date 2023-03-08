package com.github.chunhodong.awssqsclient.pool;

import com.amazonaws.services.sqs.buffered.AmazonSQSBufferedAsyncClient;
import com.github.chunhodong.awssqsclient.client.AwsSQSClient;
import com.github.chunhodong.awssqsclient.client.ProxyAwsSQSClient;
import com.github.chunhodong.awssqsclient.client.SQSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class AwsSQSClientPoolImpl implements AwsSQSClientPool {
    private final Logger logger = LoggerFactory.getLogger(AwsSQSClientPoolImpl.class);
    private final List<PoolElement> elements;
    private final ThreadLocal<LocalDateTime> clientRequestTime;
    private final PoolConfiguration poolConfig;
    private final Map<ProxyAwsSQSClient, PoolElement> proxySQSClients;
    private final AmazonSQSBufferedAsyncClient asyncClient;
    private final BlockingQueue<PoolElement> temporaryElements;

    public AwsSQSClientPoolImpl(PoolConfiguration poolConfig, AmazonSQSBufferedAsyncClient asyncClient) {
        this.poolConfig = poolConfig;
        this.asyncClient = asyncClient;
        this.elements = createElements(poolConfig, asyncClient);
        this.clientRequestTime = new ThreadLocal<>();
        this.proxySQSClients = Collections.synchronizedMap(new HashMap<>());
        this.temporaryElements = new ArrayBlockingQueue<>(poolConfig.getPoolSize(), true);
        runPoolManager();
    }

    private void runPoolManager() {
        if (poolConfig.isDefaultIdleTimeout()) {
            return;
        }
        new ElementCleaner().run();
        new ElementCreator().run();
    }

    private List<PoolElement> createElements(PoolConfiguration poolConfig, AmazonSQSBufferedAsyncClient asyncClient) {
        return new CopyOnWriteArrayList<>(Collections.nCopies(poolConfig.getPoolSize(), AwsSQSClient.createClient(asyncClient))
                .stream()
                .map(PoolElement::new)
                .collect(Collectors.toList()));
    }

    @Override
    public SQSClient getClient() {
        PoolElement entry = getElement();
        ProxyAwsSQSClient proxyAwsSQSClient = new ProxyAwsSQSClient(entry);
        this.proxySQSClients.put(proxyAwsSQSClient, entry);
        return proxyAwsSQSClient;
    }

    private PoolElement getElement() {
        clientRequestTime.set(LocalDateTime.now());
        do {
            for (PoolElement element : elements) {
                if (element.close()) {
                    clientRequestTime.remove();
                    return element;
                }
            }
            PoolElement element = newElement(asyncClient);
            if (Objects.nonNull(element)) {
                clientRequestTime.remove();
                return element;
            }
        } while (isTimeout());
        clientRequestTime.remove();
        throw new ConnectionTimeoutException();
    }

    @Override
    public void release(SQSClient sqsClient) {
        PoolElement poolElement = proxySQSClients.remove(sqsClient);
        poolElement.open();
    }

    private PoolElement newElement(AmazonSQSBufferedAsyncClient asyncClient) {
        if (elements.size() >= poolConfig.getPoolSize()) {
            return null;
        }
        PoolElement poolElement = new PoolElement(AwsSQSClient.createClient(asyncClient), ElementState.CLOSE);
        if (temporaryElements.offer(poolElement)) {
            return poolElement;
        }
        return null;
    }

    private boolean isTimeout() {
        return !poolConfig.isConnectionTimeout(clientRequestTime.get());
    }

    private class ElementCleaner extends ScheduledThreadPoolExecutor {
        private final static int DEFAULT_POOL_SIZE = 1;
        private static final int DEFAULT_INITAIL_DELAY = 1000;
        private static final int DEFAULT_DELAY_CLEANER = 30000;

        public ElementCleaner() {
            this(DEFAULT_POOL_SIZE);
        }

        private ElementCleaner(int corePoolSize) {
            super(corePoolSize);
        }

        private void cleanElement() {
            int poolSize = elements.size();
            if (poolConfig.hasMinimumPoolSize(poolSize)) {
                return;
            }
            List<PoolElement> removeElements = elements
                    .stream()
                    .filter(poolEntry -> poolEntry.isIdle(poolConfig.getIdleTimeout()))
                    .limit(poolSize - poolConfig.getMinimumPoolSize())
                    .collect(Collectors.toList());
            int idlePoolSize = removeElements.size();
            int activePoolSize = poolSize - idlePoolSize;
            logger.debug("current poolsize-{}, active poolsize-{}, idle poolsize-{}", poolSize, activePoolSize, idlePoolSize);
            elements.removeAll(removeElements);
        }

        public void run() {
            scheduleWithFixedDelay(() -> cleanElement(), DEFAULT_INITAIL_DELAY, DEFAULT_DELAY_CLEANER, TimeUnit.MILLISECONDS);
        }
    }

    private class ElementCreator extends ScheduledThreadPoolExecutor {
        private final static int DEFAULT_POOL_SIZE = 1;
        private static final int DEFAULT_INITAIL_DELAY = 1000;
        private static final int DEFAULT_DELAY_CLEANER = 20000;

        public ElementCreator() {
            this(DEFAULT_POOL_SIZE);
        }

        private ElementCreator(int corePoolSize) {
            super(corePoolSize);
        }

        private void addElement() {
            int size = poolConfig.getPoolSize() - elements.size();
            List<PoolElement> elements = temporaryElements.stream().limit(size).collect(Collectors.toList());
            elements.stream().forEach(PoolElement::open);
            temporaryElements.clear();
            elements.addAll(elements);
        }

        public void run() {
            scheduleWithFixedDelay(() -> addElement(), DEFAULT_INITAIL_DELAY, DEFAULT_DELAY_CLEANER, TimeUnit.MILLISECONDS);
        }
    }
}
