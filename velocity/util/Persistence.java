package velocity.util;

import java.util.HashMap;

/**
 * Alpha version of velocity.util.Persistence. Retains data between scenes.
 */
// TODO: Dump the contents of persistence when an error occurs.
public class Persistence {
    /**
     * The singleton persistence.
     */
    private static Persistence thePersistence = new Persistence();

    /**
     * Store the actual data on this persistence.
     */
    HashMap<String, Object> stash;

    private Persistence() {
        this.stash = new HashMap<String, Object>();
    }

    /**
     * Push an element into this persistence's storage.
     * 
     * @param key Key to access the element with.
     * @param value Element.
     */
    public static void push(String key, Object value) {
        if (thePersistence.stash.containsKey(key))
            throw new IllegalStateException("Element with key " + key + " already in persistence!");

        thePersistence.stash.put(key, value);
    }

    /**
     * Read the value and remove it from this persistence.
     * 
     * @param key The element key.
     * @return The element.
     */
    public static Object pop(String key) {
        if (!thePersistence.stash.containsKey(key))
            throw new IllegalStateException("No element found with key " + key + "!");

        return thePersistence.stash.remove(key);
    }

    /**
     * Determine whether this persistence has the element requested.
     * 
     * @param key Key of the element.
     * @return Whether a key/value pair is present.
     */
    public static boolean isPresent(String key) {
        return thePersistence.stash.containsKey(key);
    }
}
