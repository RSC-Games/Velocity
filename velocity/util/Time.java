package velocity.util;

public class Time {
    public static void sleepms(int ms) {
        try {
            Thread.sleep(ms);
        }
        catch (InterruptedException ie) {}
    }

    public static void sleep(int s) {
        sleepms(s * 1000);
    }

    //public static void sleepms_busy() {}
}
