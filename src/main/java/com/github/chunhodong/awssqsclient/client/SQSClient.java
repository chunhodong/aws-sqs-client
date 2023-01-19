package com.github.chunhodong.awssqsclient.client;

public interface SQSClient<T> {
    void send(String channel, T message);
}
