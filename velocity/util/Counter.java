package velocity.util;

public class Counter {
    private long start;

    public Counter() {
        this.start = System.nanoTime();
    }

    public long tick() {
        long tick = (System.nanoTime() - this.start);
        this.start = System.nanoTime();
        return tick;
    }

    public int tickms() {
        int tick = (int)((System.nanoTime() - this.start) / 1_000_000);
        this.start = System.nanoTime();
        return tick;
    }
}
