package com.rsc_games.velocity.sprite;

import com.rsc_games.velocity.renderer.DrawInfo;
import com.rsc_games.velocity.renderer.FrameBuffer;
import com.rsc_games.velocity.util.Transform;

/**
 * Basic renderable sprite. All sprites are automatically positioned
 * on screen relative to the current camera transform.
 */
public abstract class Renderable extends Sprite {
    /**
     * Create a renderable.
     * 
     * @param pos The renderable position.
     * @param rot The rotation angle.
     * @param name The name of the renderable.
     */
    public Renderable(Transform transform, String name) {
        super(transform, name);
    }

    /**
     * Draw the renderable on screen.
     * 
     * @param d The fully resolved sprite location in camera space.
     * @param fb The rendering framebuffer.
     */
    public abstract void render(DrawInfo d, FrameBuffer fb);
}
