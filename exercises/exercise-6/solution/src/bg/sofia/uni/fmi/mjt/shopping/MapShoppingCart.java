package bg.sofia.uni.fmi.mjt.shopping;

import bg.sofia.uni.fmi.mjt.shopping.item.Item;

import java.util.*;

public class MapShoppingCart implements ShoppingCart {

    private Map<Item, Integer> items = new HashMap<>();

    @Override
    public Collection<Item> getUniqueItems() {
        return items.keySet();
    }

    @Override
    public void addItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Given null item!");
        }

        Integer occurrences = items.get(item);
        if (occurrences == null) {
            occurrences = 0;
        }
        items.put(item, ++occurrences);
    }

    @Override
    public void removeItem(Item item) throws ItemNotFoundException {
        if (item == null) {
            throw new IllegalArgumentException("Given null item!");
        }

        if (!items.containsKey(item)) {
            throw new ItemNotFoundException();
        }
        Integer occurrences = items.get(item);
        items.put(item, --occurrences);

        if (occurrences == 0) {
            items.remove(item);
        }
    }

    @Override
    public double getTotal() {
        double total = 0;
        for (Map.Entry<Item, Integer> e : items.entrySet()) {
            total += e.getKey().getPrice() * e.getValue();
        }
        return total;
    }

    @Override
    public Collection<Item> getSortedItems() {
        TreeSet<Map.Entry<Item, Integer>> sortedItems = new TreeSet<>(new Comparator<Map.Entry<Item, Integer>>() {
            @Override
            public int compare(Map.Entry<Item, Integer> o1, Map.Entry<Item, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        sortedItems.addAll(items.entrySet());

        List<Item> result = new ArrayList<>();
        for (Map.Entry<Item, Integer> item : sortedItems) {
            result.add(item.getKey());
        }
        return result;
    }

    Map<Item, Integer> getItems() {
        return items;
    }
}