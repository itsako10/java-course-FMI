package bg.sofia.uni.fmi.mjt.cache;

import bg.sofia.uni.fmi.mjt.cache.enums.CacheHits;
import bg.sofia.uni.fmi.mjt.cache.enums.EvictionPolicy;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class RRCache<K, V> extends CacheWithNoEvictionPolicy<K, V> {

    public RRCache(long capacity, EvictionPolicy policy) {
        super(capacity, policy);
    }

    public RRCache(EvictionPolicy policy) {
        super(policy);
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

        return value;
    }

    @Override
    public void set(K key, V value) {
        if (key == null || value == null) {
            return;
        }

        if (cache.containsKey(key)) {
            cache.put(key, value);
            return;
        }

        if (isFull()) {
            freeOneElement();
        }

        cache.put(key, value);
    }

    @Override
    public long getUsesCount(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void freeOneElement() {
        Set keys = cache.keySet();
        cache.remove(keys.iterator().next());
    }
}
