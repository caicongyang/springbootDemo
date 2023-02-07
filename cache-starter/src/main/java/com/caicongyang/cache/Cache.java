package com.caicongyang.cache;

import java.util.Set;

public interface Cache {

    public Object get(String key);

    public void set(String key, Object value);

    public void add4Set(String key, Object value);

    public Set<Object> members4Set(String key);
}
