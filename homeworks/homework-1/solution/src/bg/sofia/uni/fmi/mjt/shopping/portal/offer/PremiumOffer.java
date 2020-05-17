package bg.sofia.uni.fmi.mjt.shopping.portal.offer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class PremiumOffer extends RegularOffer {
    private static final double DISCOUNT_INTERVAL_FROM = 0.00;
    private static final double DISCOUNT_INTERVAL_TO = 100.00;
    private double discount;

    public PremiumOffer(String productName, LocalDate date, String description,
                        double price, double shippingPrice, double discount) {
        super(productName, date, description, price, shippingPrice);
        setDiscount(discount);
        setTotalPrice(price, shippingPrice);
    }

    private void setDiscount(double discount) {
        BigDecimal temp = new BigDecimal(Double.toString(discount));
        temp = temp.setScale(2, RoundingMode.HALF_UP);
        discount = temp.doubleValue();

        if (discount < DISCOUNT_INTERVAL_FROM) {
            this.discount = DISCOUNT_INTERVAL_FROM;
        } else if (discount > DISCOUNT_INTERVAL_TO) {
            this.discount = DISCOUNT_INTERVAL_TO;
        } else {
            this.discount = discount;
        }
    }

    @Override
    protected void setTotalPrice(double price, double shippingPrice) {
        final int denominatorForPercentages = 100;
        totalPrice = (price + shippingPrice) - (price + shippingPrice) * (discount / denominatorForPercentages);
    }

    @Override
    public String toString() {
        return "PremiumOffer{" +
                "productName='" + getProductName() + '\'' +
                ", date=" + getDate() +
                ", description='" + getDescription() + '\'' +
                ", price=" + getPrice() +
                ", shippingPrice=" + getShippingPrice() +
                ", totalPrice=" + totalPrice +
                ", discount=" + discount +
                '}';
    }
}
