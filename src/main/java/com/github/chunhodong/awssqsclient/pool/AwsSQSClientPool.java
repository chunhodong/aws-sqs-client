package com.github.chunhodong.awssqsclient.pool;

import com.github.chunhodong.awssqsclient.client.SQSClient;

public interface AwsSQSClientPool {
    SQSClient getClient();
    void release(SQSClient sqsClient);
}
