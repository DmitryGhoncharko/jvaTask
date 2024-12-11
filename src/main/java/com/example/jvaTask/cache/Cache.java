package com.example.jvaTask.cache;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class Cache {

    private final Map<String, Map<String, Object>> cache = new ConcurrentHashMap<>();

    public Map<String, Object> get(String key) {
        return cache.get(key);
    }

    public void put(String key, Map<String, Object> value) {
        cache.put(key, value);
    }
}

