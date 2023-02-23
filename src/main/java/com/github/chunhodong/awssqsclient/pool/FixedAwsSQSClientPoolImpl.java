package com.github.chunhodong.awssqsclient.pool;

import com.amazonaws.services.sqs.buffered.AmazonSQSBufferedAsyncClient;
import com.github.chunhodong.awssqsclient.client.SQSClient;
import com.github.chunhodong.awssqsclient.utils.Timeout;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class FixedAwsSQSClientPoolImpl extends AwsSQSClientPoolImpl {

    private ReentrantLock entryLock;

    public FixedAwsSQSClientPoolImpl(List<SQSClient> clients, AmazonSQSBufferedAsyncClient asyncClient) {
        super(clients, asyncClient);
    }

    public FixedAwsSQSClientPoolImpl(Timeout connectionTimeout, Timeout idleTimeout, List<SQSClient> clients, AmazonSQSBufferedAsyncClient asyncClient) {
        super(clients, asyncClient, connectionTimeout, idleTimeout);
    }

    @Override
    protected PoolElement publishEntry() {
        return null;
    }
}
