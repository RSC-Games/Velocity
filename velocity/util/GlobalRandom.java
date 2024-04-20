package velocity.util;

import java.util.Random;

/**
 * A global random system, shared across all execution threads. Takes the hassle
 * out of generating random numbers at the cost of flexibility.
 */
public class GlobalRandom {
    /**
     * The thread local, or should I say, global, random. 
     */
    private static Random random = new Random();

    /**
     * Generate a random number in the bounds [min, max).
     * 
     * @param min Minimum bound (bound inclusive)
     * @param max Maximum bound (bound exclusive)
     * @return A pseudo-random number between min and max.
     */
    public static int randint(int min, int max) {
        return random.nextInt(min, max);
    }
}
