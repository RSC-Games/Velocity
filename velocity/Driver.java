package velocity;

/**
 * Wrapper interface for Main class. Simplifies engine init and update. Not used
 * for public-facing API.
 */
public interface Driver {
    /**
     * Run the game loop. Called generally by the event handlers.
     */
    public void gameLoop();
}
