package com.github.chunhodong.awssqsclient.pool;

import com.github.chunhodong.awssqsclient.client.SQSClient;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public class PoolElement {

    private static final AtomicIntegerFieldUpdater<PoolElement> stateUpdater;
    private final SQSClient sqsClient;
    private volatile int state;
    private long accessTime;

    static {
        stateUpdater = AtomicIntegerFieldUpdater.newUpdater(PoolElement.class, "state");
    }

    public PoolElement(SQSClient sqsClient) {
        this(sqsClient, ElementState.OPEN);
    }

    public PoolElement(SQSClient sqsClient, int state) {
        Objects.requireNonNull(sqsClient);
        this.sqsClient = sqsClient;
        this.state = state;
        accessTime = System.currentTimeMillis();
    }

    public SQSClient getSqsClient() {
        return sqsClient;
    }

    public void open() {
        accessTime = System.currentTimeMillis();
        stateUpdater.compareAndSet(this, ElementState.CLOSE, ElementState.OPEN);
    }

    public boolean close() {
        accessTime = System.currentTimeMillis();
        return stateUpdater.compareAndSet(this, ElementState.OPEN, ElementState.CLOSE);
    }

    public boolean isIdle(long idleTimeout) {
        return state == ElementState.OPEN && System.currentTimeMillis() - accessTime > idleTimeout;
    }
}

