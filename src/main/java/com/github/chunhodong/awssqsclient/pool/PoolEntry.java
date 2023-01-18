package com.github.chunhodong.awssqsclient.pool;

import com.github.chunhodong.awssqsclient.client.SQSClient;

import java.util.Objects;

public class PoolEntry {

    private final SQSClient sqsClient;

    public PoolEntry(SQSClient sqsClient) {
        Objects.nonNull(sqsClient);
        this.sqsClient = sqsClient;
    }

    public SQSClient getSqsClient() {
        return sqsClient;
    }
}
