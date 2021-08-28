package com.caicongyang.basic;

import java.util.HashMap;
import java.util.Map;

public class BaseContext<K, V> {

    protected Map<K, V> ctxMap = new HashMap<K, V>();

    public void set(K key, V value) {
        ctxMap.put(key, value);
    }

    public V get(K key) {
        return ctxMap.get(key);
    }

    public <T> T get(K key, Class<T> typeClass) {
        Object value = this.get(key);
        if (value == null) {
            return null;
        }
        return convert(this.get(key), typeClass);
    }

    protected <T> T convert(V obj, Class<T> typeClass) {
        return ValueUtils.convert(obj, typeClass);
    }
}
