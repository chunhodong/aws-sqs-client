package com.github.chunhodong.awssqsclient.pool;

import com.github.chunhodong.awssqsclient.client.SQSClient;

import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

public class PoolEntry {

    private final SQSClient sqsClient;
    private PoolEntryState state;
    private final ReentrantLock accessLock;

    public PoolEntry(SQSClient sqsClient) {
        Objects.nonNull(sqsClient);
        this.sqsClient = sqsClient;
        this.accessLock = new ReentrantLock();
        this.state = PoolEntryState.IN_NOT_USE;
    }

    public SQSClient getSqsClient() {
        return sqsClient;
    }

    public boolean isUse() {
        return state == PoolEntryState.IN_USE;
    }

    public void use() {
        state = PoolEntryState.IN_USE;
    }

    public void unuse() {
        state = PoolEntryState.IN_NOT_USE;
    }

    public void accessLock() {
        accessLock.lock();
    }

    public void releaseLock() {
        if (accessLock.isLocked()) {
            accessLock.unlock();
        }
    }
}
