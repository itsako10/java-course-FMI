package bg.sofia.uni.fmi.mjt.shopping.portal;

import bg.sofia.uni.fmi.mjt.shopping.portal.exceptions.NoOfferFoundException;
import bg.sofia.uni.fmi.mjt.shopping.portal.exceptions.OfferAlreadySubmittedException;
import bg.sofia.uni.fmi.mjt.shopping.portal.exceptions.ProductNotFoundException;
import bg.sofia.uni.fmi.mjt.shopping.portal.offer.Offer;
import bg.sofia.uni.fmi.mjt.shopping.portal.offer.OffersForProduct;

import java.time.LocalDate;
import java.util.*;

public class ShoppingDirectoryImpl implements ShoppingDirectory {
    private static final int LAST_N_DAYS = 30;
    private Map<String, OffersForProduct> offers;

    public ShoppingDirectoryImpl() {
        offers = new HashMap<>();
    }

    @Override
    public Collection<Offer> findAllOffers(String productName) throws ProductNotFoundException {
        if (productName == null) {
            throw new IllegalArgumentException("productName is null");
        }

        if (!offers.containsKey(productName)) {
            throw new ProductNotFoundException();
        }

        ArrayList<Offer> result = getLastNDaysOffers(productName);

        Collections.sort(result, new Comparator<Offer>() {
            @Override
            public int compare(Offer o1, Offer o2) {
                return Double.compare(o1.getTotalPrice(), o2.getTotalPrice());
            }
        });

        return result;
    }

    @Override
    public Offer findBestOffer(String productName) throws ProductNotFoundException, NoOfferFoundException {
        if (productName == null) {
            throw new IllegalArgumentException("product name is null");
        }

        if (!offers.containsKey(productName)) {
            throw new ProductNotFoundException();
        }

        ArrayList<Offer> lastNDaysOffers = getLastNDaysOffers(productName);

        if (lastNDaysOffers.isEmpty()) {
            throw new NoOfferFoundException();
        }

        Iterator<Offer> offerIterator = lastNDaysOffers.iterator();
        Offer bestOffer = offerIterator.next();

        while (offerIterator.hasNext()) {
            Offer i = offerIterator.next();
            if (i.getTotalPrice() < bestOffer.getTotalPrice()) {
                bestOffer = i;
            }
        }

        return bestOffer;
    }

    private ArrayList<Offer> getLastNDaysOffers(String productName) {
        LocalDate today = LocalDate.now();

        ArrayList<Offer> result = new ArrayList<>();

        Iterator<Offer> offerIterator = offers.get(productName).getOffersForProduct().iterator();

        while (offerIterator.hasNext()) {
            Offer i = offerIterator.next();
            if (today.minusDays(LAST_N_DAYS - 1).compareTo(i.getDate()) > 0) {
                break;
            }
            result.add(i);
        }

        return result;
    }

    @Override
    public void submitOffer(Offer offer) throws OfferAlreadySubmittedException {
        if (offer == null) {
            throw new IllegalArgumentException();
        }

        if (!offers.containsKey(offer.getProductName())) {
            offers.put(offer.getProductName(), new OffersForProduct());
        }

        boolean isAdded = offers.get(offer.getProductName()).addOffer(offer);
        if (!isAdded) {
            throw new OfferAlreadySubmittedException();
        }
    }

    @Override
    public Collection<PriceStatistic> collectProductStatistics(String productName) throws ProductNotFoundException {
        if (productName == null) {
            throw new IllegalArgumentException();
        }

        if (!offers.containsKey(productName)) {
            throw new ProductNotFoundException();
        }

        OffersForProduct allOffersForCurrentProduct = offers.get(productName);

        List<LocalDate> allDifferentDates = new ArrayList<>();

        Iterator<Offer> offerIterator = allOffersForCurrentProduct.getOffersForProduct().iterator();
        Offer i = offerIterator.next();
        allDifferentDates.add(i.getDate());

        Offer j;
        while (offerIterator.hasNext()) {
            j = offerIterator.next();
            if (!i.getDate().equals(j.getDate())) {
                allDifferentDates.add(j.getDate());
                i = j;
            }
        }

        List<PriceStatistic> result = new ArrayList<>();

        for (LocalDate dateIter : allDifferentDates) {
            result.add(new PriceStatistic(dateIter, allOffersForCurrentProduct));
        }

        return result;
    }
}