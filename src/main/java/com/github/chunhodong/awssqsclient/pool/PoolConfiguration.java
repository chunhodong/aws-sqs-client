package com.github.chunhodong.awssqsclient.pool;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class PoolConfiguration {
    private static final long DEFAULT_IDLE_TIMEOUT = 0;
    private static final long DEFAULT_CONNECTION_TIMEOUT = 30000;
    private static final int DEFAULT_POOL_SIZE = 10;
    private long idleTimeout = DEFAULT_IDLE_TIMEOUT;
    private long connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
    private int poolSize = DEFAULT_POOL_SIZE;

    public PoolConfiguration(){

    }

    public int getPoolSize(){
        return poolSize;
    }

    public long getIdleTimeout(){
        return idleTimeout;
    }

    private PoolConfiguration(PoolConfigurationBuilder builder) {
        this.idleTimeout = builder.idleTimeout;
        this.connectionTimeout = builder.connectionTimeout;
        this.poolSize = builder.poolSize;
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

        public PoolConfigurationBuilder idleTimeout(long idleTimeout) {
            this.idleTimeout = idleTimeout;
            return this;
        }

        public PoolConfigurationBuilder connectionTimeout(long connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
            return this;
        }

        public PoolConfigurationBuilder poolSize(int poolSize){
            this.poolSize = poolSize;
            return this;
        }

        public PoolConfiguration build() {
            return new PoolConfiguration(this);
        }
    }
}
