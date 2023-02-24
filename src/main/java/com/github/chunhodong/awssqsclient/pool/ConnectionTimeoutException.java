package com.github.chunhodong.awssqsclient.pool;

public class ConnectionTimeoutException extends RuntimeException {

    public static final String CONNECTION_TIMEOUT_EXCEPTION = "The client failed to acquire a connection due to connection timeout ";

    public ConnectionTimeoutException(){
        super(CONNECTION_TIMEOUT_EXCEPTION);
    }
}
