
import java.util.concurrent.Semaphore;

/**
 * @author Carlos Verduzco, Steven Dao
 * @version 1.0
 *
 * Date: October 25, 2021
 * Purpose: This class defines the east village thread.
 */
public class EastVillager implements Runnable {

    // the semaphore used to keep count of villagers using the road
    private final Semaphore villageRoad;
    // end the thread once the villager has used the road
    private boolean hasUsedRoad;


    /**
     * Constructs a new EastVillage instance using the provided semaphore.
     *
     * @param newRoad the new instance of the semaphore to keep count
     */
    public EastVillager(Semaphore newRoad) {
        villageRoad = newRoad;
        hasUsedRoad = false;
    }

    /**
     * Runs the thread representing the east village by simulating an activity.
     */
    @Override
    public void run() {

        // let the user know the villager thread has started running
        System.out.println(Thread.currentThread().getName() + ": Waiting to use the road ...");

        // catch InterruptedExceptions
        try {
            // end the thread once the villager has used the road
            while (!hasUsedRoad) {
                // check to see if this villager is next to use the road, and if the permit is available, acquire it
                if (Main.villagers.peek() == this && villageRoad.tryAcquire()) {
                    // generate a random action from global actions within the Main class
                    String randomizedAction = Main.actions[(int) (Math.random() * Main.actions.length)];

                    // let the user know the permit has been acquired and execute a random action
                    System.out.println(Thread.currentThread().getName() + ": Using the road; " + randomizedAction);
                    // currently using the road; have the thread sleep for a random period between the min and max
                    Thread.sleep((int) ((Math.random() * Main.sleepingMax) + Main.sleepingMin) * 1_000);

                    // let the user know the road is free for others to use
                    System.out.println(Thread.currentThread().getName() + ": Done using the road.");
                    // release the permit so other villagers can access the road
                    villageRoad.release();
                    // remove the head of the global queue to traverse through all villagers waiting to use the road
                    Main.villagers.remove();
                    // update the flag and end the loop
                    hasUsedRoad = true;
                } // else: do nothing and wait for the permit from the semaphore to be available
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
