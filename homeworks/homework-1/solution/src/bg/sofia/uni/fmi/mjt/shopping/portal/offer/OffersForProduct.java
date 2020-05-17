package bg.sofia.uni.fmi.mjt.shopping.portal.offer;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

public class OffersForProduct {
    private SortedSet<Offer> offersForProduct;

    public OffersForProduct() {
        offersForProduct = new TreeSet<>(new Comparator<Offer>() {
            public int compare(Offer o1, Offer o2) {
                if (o2.getProductName().toLowerCase().equals(o1.getProductName().toLowerCase()) &&
                        o2.getDate().equals(o1.getDate()) &&
                        Double.compare(o2.getTotalPrice(), o1.getTotalPrice()) == 0) {
                    return 0;
                }
                if (o2.getDate().compareTo(o1.getDate()) == 0) {
                    return 1;
                }
                return o2.getDate().compareTo(o1.getDate());
            }
        });
    }

    public boolean addOffer(Offer offer) {
        return offersForProduct.add(offer);
    }

    public SortedSet<Offer> getOffersForProduct() {
        return offersForProduct;
    }

    public int size() {
        return offersForProduct.size();
    }

    public boolean isEmpty() {
        return offersForProduct.isEmpty();
    }
}
