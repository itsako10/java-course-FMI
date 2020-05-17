package bg.sofia.uni.fmi.mjt.shopping.item;

import bg.sofia.uni.fmi.mjt.shopping.item.Item;

import java.util.*;

public class Apple implements Item {

    private String name;
    private String description;
    private double price;

    public Apple(String name, String desc, double price) {
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Apple apple = (Apple) o;

        return (Double.compare(apple.getPrice(), price) == 0 &&
                Objects.equals(name, apple.getName()) &&
                Objects.equals(description, apple.getDescription()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, price);
    }
}