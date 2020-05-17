package bg.sofia.uni.fmi.mjt.christmas;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Itsako
 * @version 1.0
 */
public class Workshop {

    private static final int ELFS_NUMBER = 20;
    private List<Gift> giftsToBeDone;
    private int wishCount;
    private Elf[] elves;
    private boolean isChristmasTime;

    /**
     * Creates Workshop.
     */
    public Workshop() {
        giftsToBeDone = new ArrayList<>();
        elves = new Elf[ELFS_NUMBER];
        isChristmasTime = false;

        for (int i = 0; i < ELFS_NUMBER; ++i) {
            elves[i] = new Elf(i, this);
            elves[i].start();
        }
    }

    /**
     * Adds a gift to the elves' backlog.
     * @param gift
     *        the wish gift that will be posted
     **/
    public synchronized void postWish(Gift gift) {
        if (gift == null) {
            throw new IllegalArgumentException("Null gift");
        }
        giftsToBeDone.add(gift);
        ++wishCount;
        this.notifyAll();
    }

    /**
     * Returns an array of the elves working in Santa's workshop.
     * @return Elf[]
     **/
    public Elf[] getElves() {
        return elves;
    }

    /**
     * Returns the next gift from the elves' backlog that has to be manufactured
     * @return Gift
     **/
    public synchronized Gift nextGift() {
        while (giftsToBeDone.size() == 0) {
            try {
                this.wait();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Gift gift = giftsToBeDone.get(giftsToBeDone.size() - 1);
        giftsToBeDone.remove(giftsToBeDone.size() - 1);

        return gift;
    }

    /**
     * Returns the total number of wishes sent to Santa's workshop by the kids.
     * @return int
     **/
    public int getWishCount() {
        return wishCount;
    }

    /**
     * Returns true if Christmas has come and false otherwise
     * @return boolean
     */
    public boolean isChristmasTime() {
        return isChristmasTime;
    }

    /**
     * Sets the isChristmasTime flag to true
     */
    public void itIsChristmas() {
        isChristmasTime = true;
    }

    /**
     * Returns the number of gifts that are waiting to be done
     * @return int
     */
    public synchronized int giftsWaitingToBeDone() {
        return giftsToBeDone.size();
    }

//    public static void main(String[] args) throws InterruptedException {
//        Workshop workshop = new Workshop();
//
//        Kid[] kids = new Kid[1000];
//
//        for (int i = 0; i < kids.length; ++i) {
//            kids[i] = new Kid(workshop);
//            kids[i].start();
//        }
//
////        for (int i = 0; i < kids.length; ++i) {
////            kids[i].join();
////        }
//
//        Thread.sleep(600);
//        workshop.itIsChristmas();
//
//        int producedGiftsCounter = 0;
//        for (int i = 0; i < workshop.getElves().length; ++i) {
//            producedGiftsCounter +=
//                    workshop.getElves()[i].getTotalGiftsCrafted();
//            System.out.println(workshop.getElves()[i].getElfId() + " "
//                    + workshop.getElves()[i].getTotalGiftsCrafted());
//        }
//
//
//        System.out.println("Wish count: " + workshop.getWishCount());
//        System.out.println("Produced gifts: " + producedGiftsCounter);
//        System.out.println("Gifts waiting to be done: " +
//                workshop.giftsWaitingToBeDone());
//    }
}