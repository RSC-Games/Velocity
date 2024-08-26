package velocity.util;

import java.lang.ref.WeakReference;
//import java.lang.reflect.Field;
import java.util.ArrayList;

//import sun.misc.Unsafe;
import velocity.sprite.Sprite;

/**
 * Velocity's internal memory tracing subsystem. Only active when
 * memory profiling has been enabled.
 */
public class MemTracerUtil {
    /**
     * Unsafe reference required for obtaining memory addresses of leaked
     * objects.
     */
    //private final Unsafe unsafe;

    /**
     * Current quantity of tracked sprites.
     */
    private int trackedSprites = 0;

    /**
     * Actual tracked sprite references.
     */
    private ArrayList<WeakReference<Sprite>> spriteRefs;

    /**
     * Internal memory tracker. Only one instance allowed.
     */
    private static MemTracerUtil theMemTracer;

    /**
     * Create the Memory Tracer and activate the tracing subsystem.
     */
    public MemTracerUtil() {
        if (theMemTracer != null) 
            throw new IllegalStateException("Cannot create multiple MemTracers!");

        theMemTracer = this;
        //unsafe = getUnsafeRef();
        this.spriteRefs = new ArrayList<WeakReference<Sprite>>();
    }

    /**
     * By default, we cannot obtain the Unsafe reference. With a little bit
     * of reflection, however, we can exfiltrate it.
     * 
     * @return The Java stdlib Unsafe.
     */
    /*
    private Unsafe getUnsafeRef() {
        try {
            Class<Unsafe> target = Unsafe.class;
            Field theUnsafe = target.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            return (Unsafe)theUnsafe.get(null);
        }
        // We can't access the unsafe. Oh well.
        catch (IllegalAccessException ie) {}
        catch (NoSuchFieldException ie) {}

        return null;
    }*/

    /**
     * Add a new sprite to be tracked.
     * 
     * @param spr Sprite to be tracked.
     */
    public static void trackSprite(Sprite spr) {
        theMemTracer.trackSprite0(spr);
    }

    /**
     * Add a new sprite to be tracked.
     * 
     * @param spr Sprite to be tracked.
     */
    private void trackSprite0(Sprite spr) {
        WeakReference<Sprite> ref = new WeakReference<Sprite>(spr);
        this.spriteRefs.add(ref);
        this.trackedSprites++;

        printSpriteInfo(spr);
    }

    /**
     * Add a new sprite to be tracked.
     * 
     * @param spr Sprite to be tracked.
     */
    public static void removeTracking(Sprite spr) {
        theMemTracer.removeTracking0(spr);
    }

    /**
     * Remove a sprite from tracking. Generally occurs when the sprite
     * has been GC'd and has therefore been released properly.
     * 
     * @param spr Sprite to remove tracking from.
     */
    private void removeTracking0(Sprite spr) {
        for (WeakReference<Sprite> ref : spriteRefs) {
            if (ref.get() == spr) {
                spriteRefs.remove(ref);
                break;
            }
        }
        this.trackedSprites--;

        Logger.log("velocity.system.MemTracer", "Deleted tracked sprite " 
            + spr.getClass().getSimpleName() + ". Tracking " + trackedSprites
            + " sprites."
        );
    }

    /**
     * Print the current memory tracing statistics, like allocated sprites,
     * memory addresses, and leaked allocations.
     */
    public static void printMemoryStatistics() {
        theMemTracer.printMemoryStatistics0();
    }

    /**
     * Print the current memory tracing statistics, like allocated sprites,
     * memory addresses, and leaked allocations.
     */
    private void printMemoryStatistics0() {
        // Clean out all of the GC'd weak references.
        for (int i = spriteRefs.size() - 1; i >= 0; i--) {
            if (spriteRefs.get(i).refersTo(null))
                spriteRefs.remove(i);
        }

        for (WeakReference<Sprite> ref : spriteRefs) {
            //if (ref.refersTo(null)) continue;
            Logger.warn("velocity.system.MemTracer", "FOUND LEAKED ALLOCATION!");
            printSpriteInfo(ref.get());
        }

        Time.sleepms(250);
        if (this.trackedSprites <= 0) return;

        Logger.warn("velocity.system.MemTracer", "Leaked allocation reporting finished.");
        System.out.println();
        Logger.warn("velocity.system.MemTracer", "Leaked memory allocations between "
                    + "scenes detected! Current sprite count: " + trackedSprites
                    + ".\n\tNote: This is not guaranteed to be a leaked allocation. If you have:"
                    + "\n\t - Allocated any sprites and moved them into the persistence pool."
                    + "\n\t - GC has not finished collecting when Memory Tracing is performed."
                    + "\n\tThen this may not apply to you."
                    );
    }

    /**
     * Print out sprite tracking info like memory address and repr.
     * 
     * @param spr Sprite to print info.
     */
    private void printSpriteInfo(Sprite spr){
        //Field nameField = spr.getClass().getField("name");
        //long addr = unsafe.objectFieldOffset(nameField);

        Logger.log("velocity.system.MemTracer", "Logging tracked sprite "
            + "<object " + spr.getClass().getSimpleName() + " @ 0x????????>\n"
            + "\tName: " + spr.name
        );
    } 
}
