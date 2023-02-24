package com.github.chunhodong.awssqsclient.client;


import com.github.chunhodong.awssqsclient.pool.PoolElement;

import java.util.Objects;

public class ProxyAwsSQSClient implements SQSClient {

    private SQSClient sqsClient;
    private PoolElement poolElement;

    public ProxyAwsSQSClient(PoolElement poolElement) {
        Objects.nonNull(poolElement);
        this.sqsClient = poolElement.getSqsClient();
        this.poolElement = poolElement;
    }

    @Override
    public void send(String channel, Object message) {
        sqsClient.send(channel, message);
    }
}

