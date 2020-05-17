package bg.sofia.uni.fmi.mjt.cache;

import bg.sofia.uni.fmi.mjt.cache.enums.CacheHits;
import bg.sofia.uni.fmi.mjt.cache.enums.EvictionPolicy;

import java.util.*;

public class LFUCache<K, V> extends CacheWithNoEvictionPolicy<K, V> {
    Map<K, Integer> accessCounter;

    public LFUCache(long capacity, EvictionPolicy policy) {
        super(capacity, policy);
        accessCounter = new HashMap<>();
    }

    public LFUCache(EvictionPolicy policy) {
        super(policy);
        accessCounter = new HashMap<>();
    }

    @Override
    public V get(K key) {
        V value = cache.get(key);
        if (value == null) {
            Double unsuccessfulHitsCount = cacheHitsCounter.putIfAbsent(CacheHits.UNSUCCESSFUL, 1.0);
            if (unsuccessfulHitsCount != null) {
                cacheHitsCounter.put(CacheHits.UNSUCCESSFUL, ++unsuccessfulHitsCount);
            }

            return value;
        }

        Double successfulHitsCount = cacheHitsCounter.putIfAbsent(CacheHits.SUCCESSFUL, 1.0);
        if (successfulHitsCount != null) {
            cacheHitsCounter.put(CacheHits.SUCCESSFUL, ++successfulHitsCount);
        }

        Integer currentAccessCounter = accessCounter.get(key);
        accessCounter.put(key, ++currentAccessCounter);

        return value;
    }

    @Override
    public void set(K key, V value) {
        if (key == null || value == null) {
            return;
        }

        if (isFull()) {
            freeOneElement();
        }

        if (cache.put(key, value) == null) {
            accessCounter.put(key, 1);
        } else {
            Integer currentAccessesCounter = accessCounter.get(key);
            accessCounter.put(key, ++currentAccessesCounter);
        }
    }

    @Override
    protected void freeOneElement() {
        List<Map.Entry<K, Integer>> entryList = new ArrayList<>(accessCounter.entrySet());
        Collections.sort(entryList, new AccessCounterComparator<>());
        cache.remove(entryList.get(0).getKey());
        accessCounter.remove(entryList.get(0).getKey());
    }

    @Override
    public long getUsesCount(K key) {
        Integer usesCount = accessCounter.get(key);
        if (key == null || usesCount == null)
        {
            return 0;
        }
        return usesCount;
    }
}
