package velocity.renderer;

/**
 * VXRA API: The event handler is fired by Velocity's {@code DrawTimer} system.
 * Individual renderers may have to handle more events than the timer tick alone.
 */
public interface EventHandler {
    /**
     * Execute the game tick and associated code.
     */
    public void onTimerTick();
}
