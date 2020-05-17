package bg.sofia.uni.fmi.mjt.christmas;

/**
 *
 * @author Itsako
 * @version 1.0
 */
public class Kid extends Thread {
    private Workshop workshop;

    /**
     * Creates Kid.
     * @param workshop
     *        The workshop to which the Kid sends a wish
     */
    public Kid(Workshop workshop) {
        this.workshop = workshop;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(10);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        workshop.postWish(Gift.getGift());
    }
}