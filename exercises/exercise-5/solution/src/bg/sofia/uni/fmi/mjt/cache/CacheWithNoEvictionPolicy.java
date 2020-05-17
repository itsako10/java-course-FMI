package bg.sofia.uni.fmi.mjt.cache;

import bg.sofia.uni.fmi.mjt.cache.enums.CacheHits;
import bg.sofia.uni.fmi.mjt.cache.enums.EvictionPolicy;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public abstract class CacheWithNoEvictionPolicy<K, V> implements Cache<K, V> {
    private static final int DEFAULT_MAX_CAPACITY = 10000;
    private final long capacity;
    EvictionPolicy policy;
    Map<K, V> cache;
    double hitRate;
    Map<CacheHits, Double> cacheHitsCounter;

    CacheWithNoEvictionPolicy(long capacity, EvictionPolicy policy) {
        this.capacity = capacity;
        this.policy = policy;
        this.cache = new HashMap<>((int) capacity);
        this.cacheHitsCounter = new EnumMap<CacheHits, Double>(CacheHits.class);
    }

    CacheWithNoEvictionPolicy(EvictionPolicy policy) {
        this.capacity = DEFAULT_MAX_CAPACITY;
        this.policy = policy;
        this.cache = new HashMap<>((int) capacity);
        this.cacheHitsCounter = new EnumMap<CacheHits, Double>(CacheHits.class);
    }

    @Override
    public abstract V get(K key);

    @Override
    public abstract void set(K key, V value);

    @Override
    public boolean remove(K key) {
        if (cache.remove(key) == null) {
            return false;
        }

        return true;
    }

    @Override
    public long size() {
        return cache.size();
    }

    @Override
    public void clear() {
        cache.clear();
        resetHitRate();
    }

    public void resetHitRate() {
        cacheHitsCounter.put(CacheHits.SUCCESSFUL, 0.0);
        cacheHitsCounter.put(CacheHits.UNSUCCESSFUL, 0.0);
        hitRate = 0.0;
    }

    @Override
    public double getHitRate() {
        Double successfulHits = cacheHitsCounter.get(CacheHits.SUCCESSFUL);

        if (successfulHits == null || successfulHits == 0) {
            successfulHits = 0.0;
            return 0;
        }

        Double unsuccessfulHits = cacheHitsCounter.get(CacheHits.UNSUCCESSFUL);

        if (unsuccessfulHits == null || unsuccessfulHits == 0) {
            unsuccessfulHits = 0.0;
            return 1;
        }

        double allHits = successfulHits + unsuccessfulHits;

        return successfulHits / allHits;
    }

    @Override
    public abstract long getUsesCount(K key);

    public boolean isFull() {
        return (size() == capacity);
    }

    protected abstract void freeOneElement();
}