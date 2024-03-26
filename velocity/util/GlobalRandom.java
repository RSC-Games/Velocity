package velocity.util;

import java.util.Random;

public class GlobalRandom {
    private static Random random = new Random();

    /**
     * Generate a random number in the bounds [min, max).
     * @param min
     * @param max
     */
    public static int randint(int min, int max) {
        return random.nextInt(min, max);
    }
}
