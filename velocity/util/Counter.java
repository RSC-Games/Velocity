package velocity.util;

/**
 * A counter for tracking elapsed time. Useful for profiling.
 */
public class Counter {
    /**
     * Starting offset for time tracking.
     */
    private long start;

    /**
     * Create the counter and set the offset at the current system counter
     * time.
     */
    public Counter() {
        this.start = System.nanoTime();
    }

    /**
     * Get the elapsed time, in nanoseconds, since the last time the timer tick
     * was called.
     * 
     * @return The time elapsed, in nanoseconds.
     */
    public long tick() {
        long tick = (System.nanoTime() - this.start);
        this.start = System.nanoTime();
        return tick;
    }

    /**
     * Get the elapsed time, in milliseconds, since the last time the timer tick
     * was called.
     * 
     * @return The time elapsed, in milliseconds.
     */
    public int tickms() {
        int tick = (int)((System.nanoTime() - this.start) / 1_000_000);
        this.start = System.nanoTime();
        return tick;
    }
}
