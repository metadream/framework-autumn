package com.arraywork.springforce.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import lombok.Getter;

/**
 * Expiring Cache
 *
 * @author ChatGPT 3.5
 * @copyright ArrayWork Inc.
 * @since 2024/02/20
 */
@Component
public class ExpiringCache<K, V> {

    private final Map<K, CacheEntry<V>> cache = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public void put(K key, V value) {
        cache.put(key, new CacheEntry<>(value, 0));
    }

    public void put(K key, V value, long duration) {
        put(key, value, duration, TimeUnit.SECONDS);
    }

    public void put(K key, V value, long duration, TimeUnit timeUnit) {
        long expirationTime = System.currentTimeMillis() + timeUnit.toMillis(duration);
        cache.put(key, new CacheEntry<>(value, expirationTime));
        scheduleCleanup();
    }

    public V get(K key) {
        if (key == null) return null;

        CacheEntry<V> entry = cache.get(key);
        if (entry != null && !entry.isExpired()) {
            return entry.value();
        }
        cache.remove(key);
        return null;
    }

    public void remove(K key) {
        cache.remove(key);
    }

    private void scheduleCleanup() {
        scheduler.schedule(this::cleanup, 1, TimeUnit.MINUTES);
    }

    private void cleanup() {
        long currentTime = System.currentTimeMillis();
        cache.entrySet().removeIf(entry -> entry.getValue().isExpired(currentTime));
        scheduleCleanup();
    }

    // Cache Entry with Expiration Time
    private record CacheEntry<T>(@Getter T value, long expirationTime) {

        public boolean isExpired() {
            return isExpired(System.currentTimeMillis());
        }

        public boolean isExpired(long currentTime) {
            return expirationTime > 0 && currentTime > expirationTime;
        }

    }

}