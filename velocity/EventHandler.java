package velocity;

/**
 * Formerly part of the VXRA API. Has been removed and made part of the Velocity core.
 * No renderer should depend on this any longer.
 */
interface EventHandler {
    /**
     * Execute the game tick and associated code.
     */
    public void onTimerTick();
}
