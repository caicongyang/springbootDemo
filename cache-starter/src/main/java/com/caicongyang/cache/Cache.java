package com.caicongyang.cache;

import java.util.Set;

public interface Cache {

    Object get(String key);

    void set(String key, Object value);

    void set(String key, Object value, long timeoutSeconds);

    void add4Set(String key, Object value);

    Set<Object> members4Set(String key);

    long size(String key);

    boolean exists(String key);

    boolean delete(String key);
}
