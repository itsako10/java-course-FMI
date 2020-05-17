package bg.sofia.uni.fmi.mjt.christmas;

import java.util.Random;

/**
 *
 * @author Itsako
 * @version 1.0
 */
public enum Gift {

    /**
     * Bike
     */
    BIKE("Bicycle", 50),

    /**
     * Car
     */
    CAR("Car", 10),

    /**
     * Doll
     */
    DOLL("Barbie doll", 6),

    /**
     * Puzzle
     */
    PUZZLE("Puzzle", 15);

    private final String type;
    private final int craftTime;

    private static Gift[] gifts = Gift.values();

    private static Random giftRand = new Random();

    /**
     * Creates Gift
     * @param type
     * @param craftTime
     */
    private Gift(String type, int craftTime) {
        this.type = type;
        this.craftTime = craftTime;
    }

    /**
     * Returns the type of gift
     * @return String
     */
    public String getType() {
        return type;
    }

    /**
     * Returns time needed to produce the gift
     * @return int
     */
    public int getCraftTime() {
        return craftTime;
    }

    /**
     * Returns a random gift
     * @return Gift
     */
    public static Gift getGift() {
        return gifts[giftRand.nextInt(gifts.length)];
    }

}