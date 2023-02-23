package com.github.chunhodong.awssqsclient.pool;

import com.amazonaws.services.sqs.buffered.AmazonSQSBufferedAsyncClient;
import com.github.chunhodong.awssqsclient.client.SQSClient;
import com.github.chunhodong.awssqsclient.utils.Timeout;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class FixedAwsSQSClientPool extends DefaultAwsSQSClientPool {

    private ReentrantLock entryLock;

    public FixedAwsSQSClientPool(List<SQSClient> clients, AmazonSQSBufferedAsyncClient asyncClient) {
        super(clients, asyncClient);
    }

    public FixedAwsSQSClientPool(Timeout connectionTimeout, Timeout idleTimeout, List<SQSClient> clients, AmazonSQSBufferedAsyncClient asyncClient) {
        super(clients, asyncClient, connectionTimeout, idleTimeout);
    }

    @Override
    protected PoolEntry publishEntry() {
        return null;
    }
}
