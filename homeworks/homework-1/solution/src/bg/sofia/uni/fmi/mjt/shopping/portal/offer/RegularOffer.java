package bg.sofia.uni.fmi.mjt.shopping.portal.offer;

import java.time.LocalDate;
import java.util.Objects;

public class RegularOffer implements Offer {
    private String productName;
    private LocalDate date;
    private String description;
    private double price;
    private double shippingPrice;
    protected double totalPrice;

    public RegularOffer(String productName, LocalDate date, String description, double price, double shippingPrice) {
        this.productName = productName;
        this.date = date;
        this.description = description;
        this.price = price;
        this.shippingPrice = shippingPrice;
        setTotalPrice(price, shippingPrice);
    }

    @Override
    public String getProductName() {
        return productName;
    }

    @Override
    public LocalDate getDate() {
        return date;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public double getShippingPrice() {
        return shippingPrice;
    }

    @Override
    public double getTotalPrice() {
        return totalPrice;
    }

    protected void setTotalPrice(double price, double shippingPrice) {
        totalPrice = price + shippingPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RegularOffer that = (RegularOffer) o;

        return Objects.equals(productName.toLowerCase(), that.productName.toLowerCase()) &&
                Objects.equals(date, that.date) &&
                Double.compare(that.totalPrice, totalPrice) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(productName.toLowerCase(), date, totalPrice);
    }

    @Override
    public String toString() {
        return "RegularOffer{" +
                "productName='" + productName + '\'' +
                ", date=" + date +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", shippingPrice=" + shippingPrice +
                ", totalPrice=" + totalPrice +
                '}';
    }
}
