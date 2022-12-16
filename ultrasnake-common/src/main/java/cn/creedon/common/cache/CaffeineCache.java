package cn.creedon.common.cache;

import com.github.benmanes.caffeine.cache.*;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/***
 * *  _ ___ ___.__   __                  _________              __         ©
 * * |_   | |  |\  |_/  |_____________   /   _____/ ____ _____  |  | __ ____
 * *   |  | |  ||  |\   __\_  __ \__  \  \_____  \ /    \\__  \ |  |/ // __ \
 * *   |  |_|  /|  |_|  |  |  | \// __ \_/        \   |  \/ __ \|    <\  ___/
 * *   |______/ |____/__|  |__|  (____  /_______  /___|  (____  /__|_ \\___  >
 * *   UltraSnake - WDC               \/        \/     \/     \/     \/    \/
 * *
 * *   功能描述：Caffeine 本地缓存
 * *
 * *   @DATE    2022/11/18
 * *   @AUTHOR  WD.C
 ***/
public class CaffeineCache<K, V> implements Cache<K, V> {

    private final String id;

    private final com.github.benmanes.caffeine.cache.Cache<K, V> cache;

    public CaffeineCache(String id) {
        this.id = id;
        @NonNull Caffeine<Object, Object> builder = Caffeine.newBuilder();
        builder.initialCapacity(64);
        builder.maximumSize(128);
        builder.expireAfter(new Expiry<K, CacheDataWrapper<V>>() {
            private long getRestTimeInNanos(CacheDataWrapper<V> value) {
                long ttl = value.getExpireTime() - System.currentTimeMillis();
                return TimeUnit.MILLISECONDS.toNanos(ttl);
            }
            @Override
            public long expireAfterCreate(K key, CacheDataWrapper<V> value, long currentTime) {
                return getRestTimeInNanos(value);
            }
            @Override
            public long expireAfterUpdate(K key, CacheDataWrapper<V> value,
                                          long currentTime, long currentDuration) {
                return currentDuration;
            }
            public long expireAfterRead(K key, CacheDataWrapper<V> value,
                                        long currentTime, long currentDuration) {
                return getRestTimeInNanos(value);
            }
        });
        this.cache = builder.build();
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void put(K key, V value) {
        this.cache.put(key, value);
    }

    @Override
    public V get(K key) {
        return this.cache.getIfPresent(key);
    }

    @Override
    public V remove(K key) {
        V obj = get(key);
        if (obj != null) {
            this.cache.invalidate(key);
        }
        return obj;
    }

    public void invalidateAll(List<K> keyList) {
        if (keyList != null) {
            this.cache.invalidateAll(keyList);
        }
    }

    @Override
    public void clear() {
        this.cache.invalidateAll();
    }

    @Override
    public long size() {
        return this.cache.estimatedSize();
    }

}
