package velocity.util;

/**
 * Velocity's basic event-driven timer system. Allows easy time tracking
 * and timer firing in game code. For only tracking elapsed time @see
 * util.Counter.
 */
public class Timer {
    /**
     * Unit for nanoseconds. No conversion required.
     */
    public static final int DUR_NS = 0;

    /**
     * Unit for microseconds. Convert from ns.
     */
    public static final int DUR_US = 1_000;

    /**
     * Unit for milliseconds. Convert from ns.
     */
    public static final int DUR_MS = 1_000_000;

    /**
     * Unit for seconds. Convert from ns.
     */
    public static final int DUR_S = 1_000_000_000;

    /**
     * Internal time offset for timer comparisons.
     */
    private long start;

    /**
     * Duration for this timer to run.
     */
    private long dur;

    /**
     * Timestamp for this timer to end.
     */
    private long end;

    /**
     * Whether this timer loops or waits for manual reset.
     */
    private boolean recurring;
    
    /**
     * Create a new timer that fires after the given time interval. If requested,
     * the timer will be reset automatically and the timer will re-run.
     * 
     * @param dms Timer duration (milliseconds)
     * @param recurring Whether the timer loops.
     */
    public Timer(int dms, boolean recurring) {
        this.start = System.nanoTime();
        this.dur = dms * (long)DUR_MS;
        this.end = this.start + this.dur;
        this.recurring = recurring;
    }

    /**
     * Create a new timer that fires after the given time interval. If requested,
     * the timer will be reset automatically and the timer will re-run. This allows
     * more fine-grained control over the timer duration.
     * 
     * @param dms Duration (in an arbitrarily-defined unit; defaults to ns)
     * @param mult Multiplier (changes the unit of the timer.)
     * @param recurring Whether this timer automatically resets.
     */
    public Timer(long dms, int mult, boolean recurring) {
        this.start = System.nanoTime();
        this.dur = dms * (long)mult;
        this.end = this.start + this.dur;
        this.recurring = recurring;
    }

    /**
     * Set this timer's duration.
     * 
     * @param dur New duration.
     */
    public void setDuration(int dur) {
        this.dur = dur * (long)DUR_MS;
    }

    /**
     * Reset this timer and restart the counter.
     */
    public void reset() {
        this.start = System.nanoTime();
        this.end = this.start + this.dur;
    }

    /**
     * Poll the timer. This is the only way to know whether the timer has
     * fired or not. Will return true once the timer has finished counting.
     * 
     * @return Whether this timer has fired or not.
     */
    public boolean tick() {
        if (System.nanoTime() <= this.end)
            return false;

        if (recurring)
            this.reset();
        
        return true;
    }
}
