package velocity.renderer;

import velocity.util.Time;

/**
 * Not meant to be inherited from! Implements the basic Velocity
 * redraw timer.
 */
public final class DrawTimer {
    private static final long MS_TO_NS = 1_000_000;
    long cns;
    long waitns;
    EventHandler a;

    public DrawTimer(int ms, EventHandler a) {
        this.a = a;
        this.waitns = ms * MS_TO_NS;
        this.cns = System.nanoTime();
    }

    // Runs the game tick and blocks until the timer expires.
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
