package bg.sofia.uni.fmi.mjt.cache;

import java.util.Comparator;
import java.util.Map;

public class AccessCounterComparator<K> implements Comparator<Map.Entry<K, Integer>> {
    @Override
    public int compare(Map.Entry<K, Integer> o1, Map.Entry<K, Integer> o2) {
        if (o1.getValue() < o2.getValue()) {
            return -1;
        } else if (o1.getValue() > o2.getValue()) {
            return 1;
        } else {
            return 0;
        }
    }
}
