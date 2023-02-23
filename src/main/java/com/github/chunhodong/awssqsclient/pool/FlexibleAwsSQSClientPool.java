package com.github.chunhodong.awssqsclient.pool;

import com.amazonaws.services.sqs.buffered.AmazonSQSBufferedAsyncClient;
import com.github.chunhodong.awssqsclient.client.SQSClient;
import com.github.chunhodong.awssqsclient.utils.Timeout;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class FlexibleAwsSQSClientPool extends DefaultAwsSQSClientPool {

    private final int maxPoolsize;
    private final ScheduledExecutorService idlePoolCleaner;
    private static final int DEFAULT_CLEANER_THREAD_SIZE = 1;
    private static final int DEFAULT_INITAIL_DELAY_CLEANER = 1000;
    private static final int DEFAULT_DELAY_CLEANER = 30000;

    public FlexibleAwsSQSClientPool(int maxPoolSize, List<SQSClient> clients, AmazonSQSBufferedAsyncClient asyncClient) {
        super(clients, asyncClient);
        this.maxPoolsize = maxPoolSize;
        idlePoolCleaner = createPoolCleaner();
        idlePoolCleaner.scheduleWithFixedDelay(new PoolEntryCleaner(), DEFAULT_INITAIL_DELAY_CLEANER, DEFAULT_DELAY_CLEANER, MILLISECONDS);
    }

    public FlexibleAwsSQSClientPool(int maxPoolSize, Timeout connectionTimeout, Timeout idleTimeout, List<SQSClient> clients, AmazonSQSBufferedAsyncClient asyncClient) {
        super(clients, asyncClient, connectionTimeout, idleTimeout);
        this.maxPoolsize = maxPoolSize;
        idlePoolCleaner = createPoolCleaner();
        idlePoolCleaner.scheduleWithFixedDelay(new PoolEntryCleaner(), DEFAULT_INITAIL_DELAY_CLEANER, DEFAULT_DELAY_CLEANER, MILLISECONDS);
    }

    private ScheduledExecutorService createPoolCleaner() {
        return new ScheduledThreadPoolExecutor(DEFAULT_CLEANER_THREAD_SIZE);
    }

    @Override
    protected PoolEntry publishEntry() {
        synchronized (lock) {
            if (getPoolSize() < maxPoolsize) {
                PoolEntry entry = newEntry(PoolEntryState.CLOSE);
                addEntry(entry);
                return entry;
            }
            return null;
        }
    }

    private final class PoolEntryCleaner implements Runnable {
        @Override
        public void run() {
            removeIdleEntry();
        }
    }
}
