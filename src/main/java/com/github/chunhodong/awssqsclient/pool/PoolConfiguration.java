package com.github.chunhodong.awssqsclient.pool;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static com.github.chunhodong.awssqsclient.pool.PoolException.MIN_IDLE_TIMEOUT_EXCEPTION;
import static com.github.chunhodong.awssqsclient.pool.PoolException.NEGATIVE_IDLE_TIMEOUT_EXCEPTION;

public class PoolConfiguration {
    private static final long MIN_IDLE_TIMEOUT = 10000;
    private static final long MIN_CONNECTION_TIMEOUT = 10000;
    private static final long DEFAULT_IDLE_TIMEOUT = 0;
    private static final long DEFAULT_CONNECTION_TIMEOUT = 30000;
    private static final int DEFAULT_POOL_SIZE = 10;
    private long idleTimeout = DEFAULT_IDLE_TIMEOUT;
    private long connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
    private int poolSize = DEFAULT_POOL_SIZE;

    public PoolConfiguration() {

    }

    public int getPoolSize() {
        return poolSize;
    }

    public long getIdleTimeout() {
        return idleTimeout;
    }

    public boolean isDefaultIdleTimeout(){
        return idleTimeout == DEFAULT_IDLE_TIMEOUT;
    }

    private PoolConfiguration(PoolConfigurationBuilder builder) {
        validationConfiguration(builder);
        this.idleTimeout = builder.idleTimeout;
        this.connectionTimeout = builder.connectionTimeout;
        this.poolSize = builder.poolSize;
    }

    private void validationConfiguration(PoolConfigurationBuilder builder) {
        validationIdleTimeout(builder.idleTimeout);
    }

    private void validationIdleTimeout(long idleTimeout) {
        if(idleTimeout < DEFAULT_IDLE_TIMEOUT){
            throw new PoolException(NEGATIVE_IDLE_TIMEOUT_EXCEPTION);
        }
        if(idleTimeout > DEFAULT_IDLE_TIMEOUT && idleTimeout < MIN_IDLE_TIMEOUT){
            throw new PoolException(MIN_IDLE_TIMEOUT_EXCEPTION);
        }
    }

    public static PoolConfigurationBuilder builder() {
        return new PoolConfigurationBuilder();
    }

    public boolean isConnectionTimeout(LocalDateTime dateTime) {
        return Objects.isNull(dateTime) ? false : ChronoUnit.MILLIS.between(dateTime, LocalDateTime.now()) > connectionTimeout;
    }

    public static class PoolConfigurationBuilder {
        private long idleTimeout = DEFAULT_IDLE_TIMEOUT;
        private long connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
        private int poolSize = DEFAULT_POOL_SIZE;

        /**
         * The number of milliseconds an idle pool element lives.
         * element in the 'open' state are removed from the pool by a cleaner thread after idletime
         * default is 0. created pool element are permanently alive.
         */
        public PoolConfigurationBuilder idleTimeout(long idleTimeout) {
            this.idleTimeout = idleTimeout;
            return this;
        }

        /**
         * The maximum number of milliseconds that a client will wait for a connection from the pool.
         * If this time is exceeded without a connection becoming available, a ConnectionTimeoutException will be thrown from
         */
        public PoolConfigurationBuilder connectionTimeout(long connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
            return this;
        }

        public PoolConfigurationBuilder poolSize(int poolSize) {
            this.poolSize = poolSize;
            return this;
        }

        public PoolConfiguration build() {
            return new PoolConfiguration(this);
        }
    }
}
