package bg.sofia.uni.fmi.mjt.shopping.portal;

import bg.sofia.uni.fmi.mjt.shopping.portal.offer.Offer;
import bg.sofia.uni.fmi.mjt.shopping.portal.offer.OffersForProduct;

import java.time.LocalDate;
import java.util.*;

public class PriceStatistic {
    private LocalDate date;
    private OffersForProduct offersForProductForDate;

    public PriceStatistic(LocalDate date, OffersForProduct offersForProduct) {
        this.date = date;
        this.offersForProductForDate = getOffersForProductForSpecificDate(date, offersForProduct);
    }

    /**
     * Returns the date for which the statistic is
     * collected.
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns the lowest total price from the offers
     * for this product for the specific date.
     */
    public double getLowestPrice() {
        Iterator<Offer> offerIterator = offersForProductForDate.getOffersForProduct().iterator();
        double lowestTotal = offerIterator.next().getTotalPrice();

        while (offerIterator.hasNext()) {
            double i = offerIterator.next().getTotalPrice();
            if (i < lowestTotal) {
                lowestTotal = i;
            }
        }

        return lowestTotal;
    }

    /**
     * Return the average total price from the offers
     * for this product for the specific date.
     */
    public double getAveragePrice() {
        double sumOfAllTotalPrices = 0;
        double countOfAllTotalPrices = offersForProductForDate.size();

        for (Offer i : offersForProductForDate.getOffersForProduct()) {
            sumOfAllTotalPrices += i.getTotalPrice();
        }

        return sumOfAllTotalPrices / countOfAllTotalPrices;
    }

    private OffersForProduct getOffersForProductForSpecificDate(LocalDate date,
                                                                OffersForProduct offersForProduct) {
        OffersForProduct result = new OffersForProduct();
        Iterator<Offer> offerIterator = offersForProduct.getOffersForProduct().iterator();

        Offer i;

        if (offerIterator.hasNext()) {
            i = offerIterator.next();
        } else {
            return result;
        }

        //if there is no Offer with this date, returns empty OffersForProduct
        //if there is a Offer with this date, the loop will break and the first Offer with this date is stored in "i"
        while (!i.getDate().equals(date)) {
            if (offerIterator.hasNext()) {
                i = offerIterator.next();
            } else {
                return result;
            }
        }

        //add to result all Offer, which are with this date
        while (i.getDate().equals(date)) {
            result.addOffer(i);
            if (offerIterator.hasNext()) {
                i = offerIterator.next();
            } else {
                break;
            }
        }

        //there is no point in continuing the loop through all other Offer, because
        //their date is before the date which we are searching for

        return result;
    }
}