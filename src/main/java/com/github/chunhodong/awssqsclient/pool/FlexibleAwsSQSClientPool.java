package com.github.chunhodong.awssqsclient.pool;

import com.amazonaws.services.sqs.buffered.AmazonSQSBufferedAsyncClient;
import com.github.chunhodong.awssqsclient.client.SQSClient;
import com.github.chunhodong.awssqsclient.utils.Timeout;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class FlexibleAwsSQSClientPool extends DefaultAwsSQSClientPool {

    private final int maxPoolsize;
    private ReentrantLock entryLock;

    public FlexibleAwsSQSClientPool(int maxPoolSize, List<SQSClient> clients, AmazonSQSBufferedAsyncClient asyncClient) {
        super(clients, asyncClient);
        this.maxPoolsize = maxPoolSize;
        this.entryLock = new ReentrantLock(true);
    }

    public FlexibleAwsSQSClientPool(int maxPoolSize, Timeout connectionTimeout, Timeout idleTimeout, List<SQSClient> clients, AmazonSQSBufferedAsyncClient asyncClient) {
        super(clients, asyncClient, connectionTimeout, idleTimeout);
        this.maxPoolsize = maxPoolSize;
        this.entryLock = new ReentrantLock(true);
    }

    @Override
    protected PoolEntry createEntry() {
        if (getPoolSize() >= maxPoolsize) return null;
        synchronized (this) {
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
        }
    }
}
