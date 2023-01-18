package com.github.chunhodong.awssqsclient.client;


import com.github.chunhodong.awssqsclient.pool.PoolEntry;

import java.util.Objects;

public class ProxyAwsSQSClient implements SQSClient {

    private SQSClient sqsClient;
    private PoolEntry poolEntry;

    public ProxyAwsSQSClient(PoolEntry poolEntry) {
        Objects.nonNull(poolEntry);
        this.sqsClient = poolEntry.getSqsClient();
        this.poolEntry = poolEntry;
    }

    @Override
    public void send(String channel, Object pushMessage) {

    }
}

