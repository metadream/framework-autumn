package com.arraywork.deps.util;

import java.util.concurrent.ConcurrentHashMap;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Expirable Cache
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/02/07
 */
public class ExpirableCache extends ConcurrentHashMap<String, Object> {

    private static final long serialVersionUID = 340333465829356167L;
    private long expires; // milliseconds

    public ExpirableCache(long expires) { // seconds
        this.expires = expires * 1000;
    }

    public Object put(String key, Object value) {
        long timestamp = System.currentTimeMillis() + expires;
        return super.put(key, new ExpirableObject(value, timestamp));
    }

    public Object get(String key) {
        if (key == null) return null;

        ExpirableObject object = (ExpirableObject) super.get(key);
        if (object != null && !object.isExpired()) {
            return object.value;
        }
        super.remove(key);
        return null;
    }

    @Data
    @AllArgsConstructor
    class ExpirableObject {

        private Object value;
        private long timestamp;

        public boolean isExpired() {
            return timestamp < System.currentTimeMillis();
        }

    }

}