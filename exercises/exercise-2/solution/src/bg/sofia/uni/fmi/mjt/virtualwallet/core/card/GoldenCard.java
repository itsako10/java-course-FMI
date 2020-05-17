package bg.sofia.uni.fmi.mjt.virtualwallet.core.card;

public class GoldenCard extends Card {

    public GoldenCard(String name) {
        super(name);
    }
    @Override
    public boolean executePayment(double cost) {
        if(cost < 0 || getAmount() < cost) {
            return false;
        }
        double real_cost = cost - (cost * 15 / 100);
        setAmount(getAmount() - real_cost);
        return true;
    }
}
