package bg.sofia.uni.fmi.mjt.virtualwallet.core.card;

public abstract class Card {
    private String name;
    private double amount;

    public Card(String name) {
        this.name = name;
    }

    public abstract boolean executePayment(double cost);

    public String getName() {
        return this.name;
    }

    public double getAmount() {
        return amount;
    }

    public boolean setAmount(double amount) {
        if (amount < 0) {
            return false;
        }
        this.amount = amount;
        return true;
    }
}