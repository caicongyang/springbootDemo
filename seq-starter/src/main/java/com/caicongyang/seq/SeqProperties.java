package com.caicongyang.seq;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties for seq-starter.
 */
@ConfigurationProperties(prefix = "seq")
public class SeqProperties {

    /**
     * Seq type: snowflake, db, redis. Default is snowflake.
     */
    private String type = "snowflake";

    /**
     * Snowflake worker id (0-31).
     */
    private long workerId = 1;

    /**
     * Snowflake datacenter id (0-31).
     */
    private long datacenterId = 1;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(long workerId) {
        this.workerId = workerId;
    }

    public long getDatacenterId() {
        return datacenterId;
    }

    public void setDatacenterId(long datacenterId) {
        this.datacenterId = datacenterId;
    }
}
