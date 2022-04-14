
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * @author Carlos Verduzco, Steven Dao
 * @version 1.0
 *
 * Date: October 25, 2021
 * Purpose: This class starts the synchronization between the EastVillage and WestVillage threads.
 *          The goal is to allow the two threads types to access the road intermittently without
 *          causing deadlock. The solution must implement semaphores and/or mutex locks.
 */
public class Main {

    // global variables
    // total number of villagers of each village type
    public static int numOfVillagers = 100;
    // lower bound for sleeping threads
    public static int sleepingMin = 1;
    // upper bound for sleeping threads
    public static int sleepingMax = 3;

    // the queue of villagers waiting to use the road;
    // use a blocking queue in order to allow synchronization upon addition / removal of villagers to the queue
    public static BlockingQueue<Object> villagers = new ArrayBlockingQueue<>(numOfVillagers * 2);
    // have the villager perform an action while using the road
    public static String[] actions = new String[] {
            "thinking about having dinner with some philosophers ...",
            "contemplating efficient ways in preventing deadlock ...",
            "chewing on some beef jerky and drinking a diet coke ..."
    };


    /**
     * Executes the application.
     *
     * @param args the command-line arguments to the application
     */
    public static void main(String[] args) {

        // create random orders for each of the villages before we add them to our queues
        ArrayList<Integer> randomOrderEast = new ArrayList<>();
        ArrayList<Integer> randomOrderWest = new ArrayList<>();

        // start with adding the range up to our maximum number of villagers
        for (int i = 0; i < numOfVillagers; ++i) {
            randomOrderEast.add(i);
            randomOrderWest.add(i);
        }

        // once all integer IDs are in our ArrayList, shuffle them so that the order is randomized
        Collections.shuffle(randomOrderEast);
        Collections.shuffle(randomOrderWest);


        // the semaphore keeping track of the number of villagers using the road
        Semaphore road = new Semaphore(1, true);

        // create a villager object for each type of village according the number of villagers
        for (int i = 0; i < numOfVillagers; ++i) {
            // catch InterruptedExceptions
            try {
                // create new villager instances using the shared semaphore
                EastVillager currentEastVillager = new EastVillager(road);
                WestVillager currentWestVillager = new WestVillager(road);

                // add the villager instances to the global queue of villagers
                villagers.put(currentEastVillager);
                villagers.put(currentWestVillager);

                Thread currentThread;

                // create a new Thread for each EastVillager;
                // name it starting from 1 instead of 0 for console readability
                currentThread = new Thread(currentEastVillager,
                        "Villager " + (randomOrderEast.get(i) + 1) + " (East)");
                // immediately start the villager thread once it's been initialized
                currentThread.start();

                // create a new Thread for each WestVillager;
                // name it starting from 1 instead of 0 for console readability
                currentThread = new Thread(currentWestVillager,
                        "Villager " + (randomOrderWest.get(i) + 1) + " (West)");
                // immediately start the villager thread once it's been initialized
                currentThread.start();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
