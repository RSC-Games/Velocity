package com.rsc_games.velocity.sprite;

import com.rsc_games.velocity.renderer.DrawInfo;
import com.rsc_games.velocity.renderer.FrameBuffer;
import com.rsc_games.velocity.util.Logger;

import com.rsc_games.velocity.util.MemTracerUtil;
import com.rsc_games.velocity.util.Transform;

import com.rsc_games.velocity.config.GlobalAppConfig;

/**
 * Standard sprite representation. Required for use in a Scene.
 */
public abstract class Sprite {
    /**
     * Sprite state tracking. A sprite cannot revert to a lower-valued
     * state than the current state it is in.
     * 
     * States:
     *  PREINIT: The sprite has been constructed but not initialized. The sprite
     *      may have init() called on it but cannot be ticked or updated.
     *  ACTIVE: The sprite has been initialized and can be used normally.
     *  DELETED: The sprite has been marked as deleted and can no longer be used.
     */
    public enum State {
        PREINIT,
        ACTIVE,
        DELETED
    };

    /**
     * Current sprite state.
     */
    private State currentState;

    /**
     * The sprite position and rect.
     */
    public final Transform transform;

    /**
     * The name of the sprite.
     */
    public final String name;

    /**
     * Create a sprite.
     * 
     * @param pos The center position of the sprite.
     * @param rot The sprite's rotation angle.
     * @param name The name of the sprite.
     */
    public Sprite(Transform transform, String name) {
        this.transform = transform;
        this.name = name;

        if (GlobalAppConfig.bcfg.LOG_MEMORY)
            MemTracerUtil.trackSprite(this);
    }

    public State getCurrentState() {
        return this.currentState;
    }

    /**
     * Determine if the state in question has been reached. If it hasn't, then actions
     * for the previous state can be taken. This function is inclusive so all previously
     * reached states are counted as reached, whether or not the sprite is actively
     * in that state.
     *
     * @param state The new state to reach.
     * @return If the sprite has reached that state.
     */
    public boolean hasReachedState(State state) {
        return this.currentState.compareTo(state) <= 0;
    }

    
    /**
     * Initialize this sprite. Init is called after scene construction and all 
     * sprites have been created. Alternatively, if the sprite is created in an 
     * existing scene, init is called when the sprite is added to the scene 
     * context (via {@code Scene.currentScene.addSprite(spr)}).
     */
    public final void init() {
        if (this.hasReachedState(State.ACTIVE))
            throw new IllegalStateException("Cannot re-initialize sprite " + name + "!");

        this.currentState = State.ACTIVE;
        onInit();
    }

    /**
     * Event callback. Triggered when this sprite is initialized. User code may and should
     * extend this function.
     */
    protected void onInit() {}

    /**
     * Simulate a game tick on this sprite. Called every frame by the active
     * scene.
     */
    public final void tick() {
        if (this.hasReachedState(State.DELETED))
            throw new IllegalStateException("Cannot tick deleted sprite " + name + "!");

        onTick();
    }

    /**
     * Event callback. Triggered when the sprite is ticked (which must only occur within
     * the engine or there WILL BE DRAGONS).
     */
    protected void onTick() {}

    /**
     * Delete this sprite and deallocate all used resources. Required in certain
     * cases (where a light source or some other object must be explicitly released).
     * This is called when a scene is destroyed in the scene loading system or if
     * {@code Scene.currentScene.deleteSprite()} is called on this sprite.
     */
    public final void delete() {
        if (hasReachedState(State.DELETED))
            throw new IllegalStateException("Cannot re-delete sprite " + name + "!");

        this.currentState = State.DELETED;
        onDeleted();
    }

    /**
     * Event callback. Invoked when the sprite in question is deleted either by the engine
     * or externally.
     */
    protected void onDeleted() {}

    /**
     * Draw this sprite on the Debug Renderer canvas. Useful for a debug view.
     * 
     * @param fb The framebuffer to draw.
     * @param info The draw transform.
     */
    public void DEBUG_render(FrameBuffer fb, DrawInfo info) {}

    /**
     * If memory tracking is active, remove this sprite from the memory tracker system.
     */
    protected void finalize() throws Throwable {
        super.finalize();
        if (GlobalAppConfig.bcfg.LOG_GC)
            Logger.log("velocity.Scene.gc", "Sprite " + name + " GC'd!");

        if (GlobalAppConfig.bcfg.LOG_MEMORY)
            MemTracerUtil.removeTracking(this);
    }
}
