package velocity.renderer;

import velocity.util.Time;

/**
 * Not meant to be inherited from! Implements the basic Velocity
 * redraw timer.
 * 
 * The Velocity DrawTimer drives the core of the redraw and simulation system.
 * It drives the script scheduler and renderer code.
 */
public final class DrawTimer {
    /**
     * Convert milliseconds to nanoseconds and back.
     */
    private static final long MS_TO_NS = 1_000_000;

    /**
     * Current counter value in nanoseconds.
     */
    long cns;

    /**
     * Delay time in nanoseconds. Each timer fire is guaranteed to be spaced
     * at least this many nanoseconds apart.
     */
    long waitns;

    /**
     * The event handler that this timer will invoke.
     */
    EventHandler a;

    /**
     * Create a draw timer.
     * 
     * @param ms Milliseconds between each timer firing.
     * @param a Event handler to process frame events.
     */
    public DrawTimer(int ms, EventHandler a) {
        this.a = a;
        this.waitns = ms * MS_TO_NS;
        this.cns = System.nanoTime();
    }

    /**
     * Run the game tick, then block until waitns nanoseconds have elapsed since
     * the beginning of the call.
     */
    public void tick() {
        a.onTimerTick();
        long cntr = System.nanoTime();

        long nsToWait = waitns - (cntr - cns);
        if (nsToWait <= 0) {
            this.cns = System.nanoTime();
            return;
        }
            
        Time.sleepms((int)((nsToWait / MS_TO_NS) + 0.9f));
        this.cns = System.nanoTime();
    }
}
