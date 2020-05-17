package bg.sofia.uni.fmi.mjt.shopping;

import bg.sofia.uni.fmi.mjt.shopping.item.Apple;
import bg.sofia.uni.fmi.mjt.shopping.item.Chocolate;
import bg.sofia.uni.fmi.mjt.shopping.item.Item;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Iterator;

import static org.junit.Assert.*;

public class ListShoppingCartTest {
    private static final double DELTA_COMPARE = 0.001;
    private ListShoppingCart listShoppingCart;
    private static final Item EXPENSIVE_COOL_RED_APPLE = new Apple("Cool apple", "Red apple", 6.6);
    private static final Item EXPENSIVE_COOL_RED_APPLE_DUBLICATE = new Apple("Cool apple", "Red apple", 6.6);
    private static final Item CHEAP_NOTCOOL_BLACK_APPLE = new Apple("Not cool apple", "Black apple", 1.6);
    private static final Item EXPENSIVE_MILKA_NATURAL_CHOCOLATE = new Chocolate("Milka", "Natural chocolate", 6.0);
    private static final Item EXPENSIVE_MILKA_NATURAL_CHOCOLATE_DUBLICATE =
            new Chocolate("Milka", "Natural chocolate", 6.0);
    private static final Item CHEAP_MILKA_WHITE_CHOCOLATE = new Chocolate("Milka", "White chocolate", 5.6);

    @Before
    public void before() {
        listShoppingCart = new ListShoppingCart();
    }

    @Test
    public void testPositiveAddItem() {
        listShoppingCart.addItem(EXPENSIVE_COOL_RED_APPLE);

        assertTrue("The item is not added to the listShoppingCart",
                listShoppingCart.getUniqueItems().contains(EXPENSIVE_COOL_RED_APPLE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddItemNullItem() {
        listShoppingCart.addItem(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveItemNullItem() throws ItemNotFoundException {
        listShoppingCart.removeItem(null);
    }

    @Test(expected = ItemNotFoundException.class)
    public void testRemoveItemFromEmptyListShoppingCart() throws ItemNotFoundException {
        ListShoppingCart listShoppingCart = new ListShoppingCart();

        listShoppingCart.removeItem(EXPENSIVE_COOL_RED_APPLE);
    }

    @Test(expected = ItemNotFoundException.class)
    public void testRemoveNotFoundItem() throws ItemNotFoundException {
        listShoppingCart.addItem(EXPENSIVE_COOL_RED_APPLE);
        listShoppingCart.removeItem(CHEAP_NOTCOOL_BLACK_APPLE);
    }

    @Test
    public void testPositiveRemoveItemApple() throws ItemNotFoundException {
        listShoppingCart.addItem(EXPENSIVE_COOL_RED_APPLE);
        listShoppingCart.removeItem(EXPENSIVE_COOL_RED_APPLE);

        assertFalse("The item is not removed",
                listShoppingCart.getUniqueItems().contains(EXPENSIVE_COOL_RED_APPLE));
    }

    @Test
    public void testPositiveRemoveItemChocolate() throws ItemNotFoundException {
        listShoppingCart.addItem(EXPENSIVE_MILKA_NATURAL_CHOCOLATE);
        listShoppingCart.removeItem(EXPENSIVE_MILKA_NATURAL_CHOCOLATE);

        assertFalse("The item is not removed",
                listShoppingCart.getUniqueItems().contains(EXPENSIVE_MILKA_NATURAL_CHOCOLATE));
    }

    @Test
    public void testGetTotalOfEmptyCart() {
        ListShoppingCart listShoppingCart = new ListShoppingCart();

        final double expected = 0.0;
        assertEquals(expected, listShoppingCart.getTotal(), DELTA_COMPARE);
    }

    @Test
    public void testGetTotal() {
        listShoppingCart.addItem(EXPENSIVE_COOL_RED_APPLE);
        listShoppingCart.addItem(EXPENSIVE_COOL_RED_APPLE_DUBLICATE);
        listShoppingCart.addItem(CHEAP_NOTCOOL_BLACK_APPLE);
        listShoppingCart.addItem(EXPENSIVE_MILKA_NATURAL_CHOCOLATE);
        listShoppingCart.addItem(EXPENSIVE_MILKA_NATURAL_CHOCOLATE_DUBLICATE);
        listShoppingCart.addItem(CHEAP_MILKA_WHITE_CHOCOLATE);

        assertEquals(EXPENSIVE_COOL_RED_APPLE.getPrice() +
                        EXPENSIVE_COOL_RED_APPLE_DUBLICATE.getPrice() +
                        CHEAP_NOTCOOL_BLACK_APPLE.getPrice() +
                        EXPENSIVE_MILKA_NATURAL_CHOCOLATE.getPrice() +
                        EXPENSIVE_MILKA_NATURAL_CHOCOLATE_DUBLICATE.getPrice() +
                        CHEAP_MILKA_WHITE_CHOCOLATE.getPrice(),
                listShoppingCart.getTotal(), DELTA_COMPARE);
    }

    @Test
    public void testPositiveGetUniqueItems() {
        listShoppingCart.addItem(EXPENSIVE_COOL_RED_APPLE);
        listShoppingCart.addItem(CHEAP_NOTCOOL_BLACK_APPLE);
        listShoppingCart.addItem(EXPENSIVE_MILKA_NATURAL_CHOCOLATE);
        listShoppingCart.addItem(CHEAP_MILKA_WHITE_CHOCOLATE);

        Collection<Item> uniqueItems = listShoppingCart.getUniqueItems();

        final int expected = 4;
        assertTrue(uniqueItems.contains(EXPENSIVE_COOL_RED_APPLE) &&
                uniqueItems.contains(CHEAP_NOTCOOL_BLACK_APPLE) &&
                uniqueItems.contains(EXPENSIVE_MILKA_NATURAL_CHOCOLATE) &&
                uniqueItems.contains(CHEAP_MILKA_WHITE_CHOCOLATE) &&
                uniqueItems.size() == expected);
    }

    @Test
    public void testGetUniqueItems() {
        listShoppingCart.addItem(EXPENSIVE_COOL_RED_APPLE);
        listShoppingCart.addItem(EXPENSIVE_COOL_RED_APPLE_DUBLICATE);
        listShoppingCart.addItem(CHEAP_NOTCOOL_BLACK_APPLE);
        listShoppingCart.addItem(EXPENSIVE_MILKA_NATURAL_CHOCOLATE);
        listShoppingCart.addItem(EXPENSIVE_MILKA_NATURAL_CHOCOLATE_DUBLICATE);
        listShoppingCart.addItem(CHEAP_MILKA_WHITE_CHOCOLATE);

        Collection<Item> uniqueItems = listShoppingCart.getUniqueItems();

        final int expected = 4;
        assertTrue("getUniqueItems doesn't get the unique items!",
                uniqueItems.contains(EXPENSIVE_COOL_RED_APPLE) &&
                        uniqueItems.contains(EXPENSIVE_COOL_RED_APPLE_DUBLICATE) &&
                        uniqueItems.contains(CHEAP_NOTCOOL_BLACK_APPLE) &&
                        uniqueItems.contains(EXPENSIVE_MILKA_NATURAL_CHOCOLATE) &&
                        uniqueItems.contains(EXPENSIVE_MILKA_NATURAL_CHOCOLATE_DUBLICATE) &&
                        uniqueItems.contains(CHEAP_MILKA_WHITE_CHOCOLATE) &&
                        uniqueItems.size() == expected);
    }

    @Test
    public void testGetSortedItems() {
        listShoppingCart.addItem(EXPENSIVE_COOL_RED_APPLE);
        listShoppingCart.addItem(EXPENSIVE_COOL_RED_APPLE);
        listShoppingCart.addItem(EXPENSIVE_COOL_RED_APPLE);

        listShoppingCart.addItem(EXPENSIVE_MILKA_NATURAL_CHOCOLATE);
        listShoppingCart.addItem(EXPENSIVE_MILKA_NATURAL_CHOCOLATE);

        listShoppingCart.addItem(CHEAP_MILKA_WHITE_CHOCOLATE);

        Collection<Item> sortedItems = listShoppingCart.getSortedItems();
        Iterator<Item> itemIterator = sortedItems.iterator();

        assertEquals(itemIterator.next(), EXPENSIVE_COOL_RED_APPLE);
        assertEquals(itemIterator.next(), EXPENSIVE_MILKA_NATURAL_CHOCOLATE);
        assertEquals(itemIterator.next(), CHEAP_MILKA_WHITE_CHOCOLATE);
    }

    @Test
    public void testGetSortedItemsSingleItem() {
        listShoppingCart.addItem(EXPENSIVE_COOL_RED_APPLE);
        Collection<Item> sortedItems = listShoppingCart.getSortedItems();
        Iterator<Item> itemIterator = sortedItems.iterator();
        assertEquals(itemIterator.next(), EXPENSIVE_COOL_RED_APPLE);
    }
}
