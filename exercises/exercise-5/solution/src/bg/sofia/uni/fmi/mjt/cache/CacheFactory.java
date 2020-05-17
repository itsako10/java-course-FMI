package bg.sofia.uni.fmi.mjt.cache;

import bg.sofia.uni.fmi.mjt.cache.enums.EvictionPolicy;

public interface CacheFactory {
    /**
     * Constructs a new Cache<K, V> with the specified maximum capacity and eviction policy
     *
     * @throws //IllegalArgumentExcepion if the given capacity is less than or equal to zero
     */
    static <K, V> Cache<K, V> getInstance(long capacity, EvictionPolicy policy) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Given capacity <= 0!");
        }

        if (policy.equals(EvictionPolicy.RANDOM_REPLACEMENT)) {
            return new RRCache<K, V>(capacity, policy);
        } else if (policy.equals((EvictionPolicy.LEAST_FREQUENTLY_USED))) {
            return new LFUCache<K, V>(capacity, policy);
        }

        return null;
    }

    /**
     * Constructs a new Cache<K, V> with maximum capacity of 10_000 items and the specified eviction policy
     */
    static <K, V> Cache<K, V> getInstance(EvictionPolicy policy) {
        if (policy.equals(EvictionPolicy.RANDOM_REPLACEMENT)) {
            return new RRCache<K, V>(policy);
        } else if (policy.equals(EvictionPolicy.LEAST_FREQUENTLY_USED)) {
            return new LFUCache<K, V>(policy);
        }

        return null;
    }
}
