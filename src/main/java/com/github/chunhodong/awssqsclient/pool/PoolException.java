package com.github.chunhodong.awssqsclient.pool;

public class PoolException extends RuntimeException {

    public static final String MIN_IDLE_TIMEOUT_EXCEPTION = "idletimeout value must be more than 10000";
    public static final String NEGATIVE_IDLE_TIMEOUT_EXCEPTION = "negative value are not allowed";

    public PoolException(String message) {
        super(message);
    }
}
