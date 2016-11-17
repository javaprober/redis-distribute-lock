package com.andy.distribute.lock.util;

import com.andy.distribute.lock.exception.RedisLockException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Utility to have 'intern' - like functionality, which holds single instance of wrapper for a given key
 */
public class InternMap<K, V> {
    private final ConcurrentMap<K, V> storage = new ConcurrentHashMap<K, V>();
    private final ValueConstructor<K, V> valueConstructor;

    public interface ValueConstructor<K, V> {
        V create(K key) throws RedisLockException;
    }

    public InternMap(ValueConstructor<K, V> valueConstructor) {
        this.valueConstructor = valueConstructor;
    }

    public V interned(K key) throws RedisLockException {
        V existingKey = storage.get(key);
        V newKey = null;
        if (existingKey == null) {
            newKey = valueConstructor.create(key);
            existingKey = storage.putIfAbsent(key, newKey);
        }
        return existingKey != null ? existingKey : newKey;
    }

    public int size() {
        return storage.size();
    }
}
