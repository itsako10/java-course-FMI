package bg.sofia.uni.fmi.mjt.christmas;

import java.util.Objects;

/**
 *
 * @author Itsako
 * @version 1.0
 */
public class Elf extends Thread {
    private int id;
    private Workshop workshop;
    private int createdGiftsCounter;

    /**
     * Creates Elf.
     * @param id
     *        the id of the Elf
     * @param workshop
     *        The workshop in which the Elf works
     */
    public Elf(int id, Workshop workshop) {
        this.id = id;
        this.workshop = workshop;
    }

    /**
     * Gets a wish from the backlog and creates the wanted gift.
     */
    public void craftGift() {
        Gift giftToBeCreated = workshop.nextGift();
        try {
            Thread.sleep(giftToBeCreated.getCraftTime());
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        ++createdGiftsCounter;
    }

    /**
     * Returns the total number of gifts that the given elf has crafted.
     * @return int
     */
    public int getTotalGiftsCrafted() {
        return createdGiftsCounter;
    }

    /**
     * Returns the id of the elf.
     * @return int
     */
    public int getElfId() {
        return id;
    }

    @Override
    public void run() {
        while (!workshop.isChristmasTime()) {
            if (workshop.giftsWaitingToBeDone() != 0) {
                craftGift();
            }
            //craftGift();
        }
    }
}