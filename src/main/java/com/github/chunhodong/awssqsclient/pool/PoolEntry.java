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
        this.state = PoolEntryState.IN_NOT_USE;
        this.mutex = this;
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

    public boolean using() {
        synchronized (mutex) {
            if (state == PoolEntryState.IN_NOT_USE) {
                state = PoolEntryState.IN_USE;
                return true;
            }
        }
        return false;
    }
}
