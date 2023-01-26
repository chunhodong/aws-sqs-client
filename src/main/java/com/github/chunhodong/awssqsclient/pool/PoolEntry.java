package com.github.chunhodong.awssqsclient.pool;

import com.github.chunhodong.awssqsclient.client.SQSClient;

import java.util.Objects;

public class PoolEntry {

    private final SQSClient sqsClient;
    private PoolEntryState state;
    private final Object mutex;

    public PoolEntry(SQSClient sqsClient) {
        Objects.nonNull(sqsClient);
        this.sqsClient = sqsClient;
        this.state = PoolEntryState.OPEN;
        this.mutex = this;
    }

    public SQSClient getSqsClient() {
        return sqsClient;
    }

    public boolean isClose() {
        return state == PoolEntryState.CLOSE;
    }

    public void open() {
        state = PoolEntryState.OPEN;
    }


    public boolean close() {
        synchronized (mutex) {
            if (state == PoolEntryState.OPEN) {
                state = PoolEntryState.CLOSE;
                return true;
            }
            return false;
        }
    }

}
