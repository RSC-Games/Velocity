package velocity.util;

/**
 * Time and thread management class. Enforces time waits and sleep
 * events. Not recommended for general use since the main thread will be
 * stalled and the window will freeze.
 */
public class Time {
    /**
     * Sleep for a specified time, in milliseconds. This is a non-busy wait,
     * where the OS scheduler is informed not to reschedule the thread again
     * until the time has elapsed.
     * 
     * @param ms Time in milliseconds.
     */
    public static void sleepms(int ms) {
        try {
            Thread.sleep(ms);
        }
        catch (InterruptedException ie) {}
    }

    /**
     * Sleep for a specified time, in seconds.
     * @see velocity.util.Time Time.sleepms
     * 
     * @param s Time, in seconds.
     */
    public static void sleep(int s) {
        sleepms(s * 1000);
    }
}
