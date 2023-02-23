package com.github.chunhodong.awssqsclient.pool;

import com.github.chunhodong.awssqsclient.client.SQSClient;
import com.github.chunhodong.awssqsclient.utils.Timeout;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public class PoolElement {

    private static final AtomicIntegerFieldUpdater<PoolElement> stateUpdater;
    private final SQSClient sqsClient;
    private int state;
    private final Object mutex;
    private long accessTime;

    static {
        stateUpdater = AtomicIntegerFieldUpdater.newUpdater(PoolElement.class, "state");
    }

    public PoolElement(SQSClient sqsClient) {
        Objects.nonNull(sqsClient);
        this.sqsClient = sqsClient;
        this.state = ElementState.CLOSE;
        this.mutex = this;
    }

    public SQSClient getSqsClient() {
        return sqsClient;
    }

    public boolean isClose() {
        return state == ElementState.CLOSE;
    }

    public void open() {
        state = ElementState.OPEN;
        accessTime = System.currentTimeMillis();
    }

    public boolean close() {
        return stateUpdater.compareAndSet(this,ElementState.OPEN,ElementState.CLOSE);
    }

    public boolean isIdle(Timeout idleTimeout) {
        return state == ElementState.OPEN && System.currentTimeMillis() - accessTime > idleTimeout.toMilis();
    }

    private static class ElementState{
        public static int OPEN = 1;
        public static int CLOSE = 0;
    }

}
