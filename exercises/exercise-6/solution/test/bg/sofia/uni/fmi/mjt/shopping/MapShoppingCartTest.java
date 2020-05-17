package bg.sofia.uni.fmi.mjt.shopping;

import bg.sofia.uni.fmi.mjt.shopping.item.Apple;
import bg.sofia.uni.fmi.mjt.shopping.item.Chocolate;
import bg.sofia.uni.fmi.mjt.shopping.item.Item;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Iterator;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class MapShoppingCartTest {
    private static final double DELTA_COMPARE = 0.001;
    private MapShoppingCart mapShoppingCart;
    private static final Item EXPENSIVE_COOL_RED_APPLE = new Apple("Cool apple", "Red apple", 6.6);
    private static final Item EXPENSIVE_COOL_RED_APPLE_DUBLICATE = new Apple("Cool apple", "Red apple", 6.6);
    private static final Item CHEAP_NOTCOOL_BLACK_APPLE = new Apple("Not cool apple", "Black apple", 1.6);
    private static final Item EXPENSIVE_MILKA_NATURAL_CHOCOLATE = new Chocolate("Milka", "Natural chocolate", 6.0);
    private static final Item EXPENSIVE_MILKA_NATURAL_CHOCOLATE_DUBLICATE =
            new Chocolate("Milka", "Natural chocolate", 6.0);
    private static final Item CHEAP_MILKA_WHITE_CHOCOLATE = new Chocolate("Milka", "White chocolate", 5.6);

    @Before
    public void before() {
        mapShoppingCart = new MapShoppingCart();
    }

    @Test
    public void testPositiveAddItem() {
        mapShoppingCart.addItem(EXPENSIVE_COOL_RED_APPLE);

        assertTrue("The item is not added to the listShoppingCart",
                mapShoppingCart.getUniqueItems().contains(EXPENSIVE_COOL_RED_APPLE));
    }

    @Test
    public void testPositiveAddItemWithIncrementTheCounterOfTheItem() {
        mapShoppingCart.addItem(EXPENSIVE_COOL_RED_APPLE);
        mapShoppingCart.addItem(EXPENSIVE_COOL_RED_APPLE_DUBLICATE);

        final int expected = 2;
        assertTrue("The item is not added to the listShoppingCart",
                mapShoppingCart.getUniqueItems().contains(EXPENSIVE_COOL_RED_APPLE) &&
                        mapShoppingCart.getItems().get(EXPENSIVE_COOL_RED_APPLE) == expected);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddItemNullItem() {
        mapShoppingCart.addItem(null);
    }

    @Test
    public void testPositiveRemoveItem() throws ItemNotFoundException {
        mapShoppingCart.addItem(EXPENSIVE_COOL_RED_APPLE);

        mapShoppingCart.removeItem(EXPENSIVE_COOL_RED_APPLE);

        final int expected = 0;
        assertTrue(!mapShoppingCart.getUniqueItems().contains(EXPENSIVE_COOL_RED_APPLE) &&
                mapShoppingCart.getUniqueItems().size() == expected);
    }

    @Test
    public void testPositiveRemoveItemWithDecrementTheCounterOfTheItem() throws ItemNotFoundException {
        mapShoppingCart.addItem(EXPENSIVE_COOL_RED_APPLE);
        mapShoppingCart.addItem(EXPENSIVE_COOL_RED_APPLE_DUBLICATE);

        mapShoppingCart.removeItem(EXPENSIVE_COOL_RED_APPLE);

        final int expected = 1;
        assertTrue("The item is not removed correctly",
                mapShoppingCart.getUniqueItems().contains(EXPENSIVE_COOL_RED_APPLE) &&
                        mapShoppingCart.getItems().get(EXPENSIVE_COOL_RED_APPLE) == expected);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveItemNullItem() throws ItemNotFoundException {
        mapShoppingCart.removeItem(null);
    }

    @Test(expected = ItemNotFoundException.class)
    public void testRemoveItemFromEmptyCart() throws ItemNotFoundException {
        mapShoppingCart.removeItem(EXPENSIVE_COOL_RED_APPLE);
    }

    @Test(expected = ItemNotFoundException.class)
    public void testRemoveNotFoundItem() throws ItemNotFoundException {
        mapShoppingCart.addItem(EXPENSIVE_COOL_RED_APPLE);
        mapShoppingCart.removeItem(CHEAP_NOTCOOL_BLACK_APPLE);
    }

    @Test
    public void testGetTotalOfEmptyCart() {
        final double expected = 0.0;
        assertEquals(expected, mapShoppingCart.getTotal(), DELTA_COMPARE);
    }

    @Test
    public void testGetTotal() {
        mapShoppingCart.addItem(EXPENSIVE_COOL_RED_APPLE);
        mapShoppingCart.addItem(EXPENSIVE_COOL_RED_APPLE_DUBLICATE);
        mapShoppingCart.addItem(CHEAP_NOTCOOL_BLACK_APPLE);
        mapShoppingCart.addItem(EXPENSIVE_MILKA_NATURAL_CHOCOLATE);
        mapShoppingCart.addItem(EXPENSIVE_MILKA_NATURAL_CHOCOLATE_DUBLICATE);
        mapShoppingCart.addItem(CHEAP_MILKA_WHITE_CHOCOLATE);

        assertEquals(EXPENSIVE_COOL_RED_APPLE.getPrice() +
                        EXPENSIVE_COOL_RED_APPLE_DUBLICATE.getPrice() +
                        CHEAP_NOTCOOL_BLACK_APPLE.getPrice() +
                        EXPENSIVE_MILKA_NATURAL_CHOCOLATE.getPrice() +
                        EXPENSIVE_MILKA_NATURAL_CHOCOLATE_DUBLICATE.getPrice() +
                        CHEAP_MILKA_WHITE_CHOCOLATE.getPrice(),
                mapShoppingCart.getTotal(), DELTA_COMPARE);
    }

    @Test
    public void testPositiveGetUniqueItems() {
        mapShoppingCart.addItem(EXPENSIVE_COOL_RED_APPLE);
        mapShoppingCart.addItem(CHEAP_NOTCOOL_BLACK_APPLE);
        mapShoppingCart.addItem(EXPENSIVE_MILKA_NATURAL_CHOCOLATE);
        mapShoppingCart.addItem(CHEAP_MILKA_WHITE_CHOCOLATE);

        Collection<Item> uniqueItems = mapShoppingCart.getUniqueItems();

        final int expected = 4;
        assertTrue(uniqueItems.contains(EXPENSIVE_COOL_RED_APPLE) &&
                uniqueItems.contains(EXPENSIVE_COOL_RED_APPLE) &&
                uniqueItems.contains(EXPENSIVE_COOL_RED_APPLE) &&
                uniqueItems.contains(EXPENSIVE_COOL_RED_APPLE) &&
                uniqueItems.size() == expected);
    }

    @Test
    public void testGetUniqueItems() {
        mapShoppingCart.addItem(EXPENSIVE_COOL_RED_APPLE);
        mapShoppingCart.addItem(EXPENSIVE_COOL_RED_APPLE_DUBLICATE);
        mapShoppingCart.addItem(CHEAP_NOTCOOL_BLACK_APPLE);
        mapShoppingCart.addItem(EXPENSIVE_MILKA_NATURAL_CHOCOLATE);
        mapShoppingCart.addItem(EXPENSIVE_MILKA_NATURAL_CHOCOLATE_DUBLICATE);
        mapShoppingCart.addItem(CHEAP_MILKA_WHITE_CHOCOLATE);

        Collection<Item> uniqueItems = mapShoppingCart.getUniqueItems();

        final int expected = 4;
        assertTrue("getUniqueItems doesn't get the unique items!",
                uniqueItems.contains(EXPENSIVE_COOL_RED_APPLE) &&
                        uniqueItems.contains(EXPENSIVE_COOL_RED_APPLE_DUBLICATE) &&
                        uniqueItems.contains(CHEAP_NOTCOOL_BLACK_APPLE) &&
                        uniqueItems.contains(EXPENSIVE_MILKA_NATURAL_CHOCOLATE) &&
                        uniqueItems.contains(EXPENSIVE_MILKA_NATURAL_CHOCOLATE_DUBLICATE)
                        && uniqueItems.contains(CHEAP_MILKA_WHITE_CHOCOLATE) &&
                        uniqueItems.size() == expected);
    }

    @Test
    public void testGetSortedItems() {
        mapShoppingCart.addItem(EXPENSIVE_COOL_RED_APPLE);
        mapShoppingCart.addItem(EXPENSIVE_COOL_RED_APPLE);
        mapShoppingCart.addItem(EXPENSIVE_COOL_RED_APPLE);

        mapShoppingCart.addItem(EXPENSIVE_MILKA_NATURAL_CHOCOLATE);
        mapShoppingCart.addItem(EXPENSIVE_MILKA_NATURAL_CHOCOLATE);

        mapShoppingCart.addItem(CHEAP_MILKA_WHITE_CHOCOLATE);

        Collection<Item> sortedItems = mapShoppingCart.getSortedItems();
        Iterator<Item> itemIterator = sortedItems.iterator();

        assertEquals(itemIterator.next(), EXPENSIVE_COOL_RED_APPLE);
        assertEquals(itemIterator.next(), EXPENSIVE_MILKA_NATURAL_CHOCOLATE);
        assertEquals(itemIterator.next(), CHEAP_MILKA_WHITE_CHOCOLATE);
    }

    @Test
    public void testGetSortedItemsSingleItem() {
        mapShoppingCart.addItem(EXPENSIVE_COOL_RED_APPLE);
        Collection<Item> sortedItems = mapShoppingCart.getSortedItems();
        Iterator<Item> itemIterator = sortedItems.iterator();
        assertEquals(itemIterator.next(), EXPENSIVE_COOL_RED_APPLE);
    }
}
