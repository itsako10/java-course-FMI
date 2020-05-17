import java.util.ArrayList;
import java.util.List;

class Pair {
    private int i;
    private int j;

    Pair(int i, int j) {
        this.i = i;
        this.j = j;
    }

    int getI() {
        return i;
    }

    int getJ() {
        return j;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "i=" + i +
                ", j=" + j +
                '}';
    }
}

public class StockExchange {
    public static int maxProfit(int[] prices) {
        List<Pair> pairs = new ArrayList<>();

        for (int i = 0; i < prices.length; ++i) {
            for (int j = i + 1; j < prices.length; ++j) {
                if ((prices[j] - prices[i]) > 0) {
                    pairs.add(new Pair(i, j));
                }
            }
        }

        if (pairs.size() == 0) {
            return 0;
        } else if (pairs.size() == 1) {
            return prices[pairs.get(0).getJ()] - prices[pairs.get(0).getI()];
        }

        int max = 0;
        for (int first = 0; first < pairs.size(); ++first) {
            int firstProfit = prices[pairs.get(first).getJ()] - prices[pairs.get(first).getI()];
            if (firstProfit > max) {
                max = firstProfit;
            }

            for (int second = first + 1; second < pairs.size(); ++second) {
                if ((pairs.get(first).getI() == pairs.get(second).getI())
                        || (pairs.get(first).getJ() >= pairs.get(second).getI())) {
                    continue;
                }

                int secondProfit = prices[pairs.get(second).getJ()] - prices[pairs.get(second).getI()];

                if (firstProfit + secondProfit > max) {
                    max = firstProfit + secondProfit;
                }
            }
        }
        System.out.println(pairs);
        return max;
    }
}
