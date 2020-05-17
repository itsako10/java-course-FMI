package bg.sofia.uni.fmi.mjt.shopping.item;

import bg.sofia.uni.fmi.mjt.shopping.item.Item;

import java.util.Objects;

public class Chocolate implements Item {

    private String name;
    private String description;
    private double price;

    public Chocolate(String name, String desc, double price) {
        this.name = name;
        this.description = desc;
        this.price = price;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public double getPrice() {
        return this.price;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        Chocolate chocolate = (Chocolate) obj;

        return (Objects.equals(chocolate.getName(), this.getName()) &&
                Objects.equals(chocolate.getDescription(), this.getDescription()) &&
                Objects.equals(chocolate.getPrice(), this.getPrice()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, price);
    }
}