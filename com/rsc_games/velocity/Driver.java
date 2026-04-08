package com.rsc_games.velocity;

/**
 * Wrapper interface for Main class. Simplifies engine init and update. Not used
 * for public-facing API.
 */
@Deprecated(since="0.7.0.0", forRemoval=true)
public interface Driver {
    /**
     * Run the game loop. Called generally by the event handlers.
     */
    @Deprecated(since="0.7.0.0", forRemoval=true)
    public void gameLoop();
}
