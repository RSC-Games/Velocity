package velocity.sprite;

import velocity.renderer.DrawInfo;
import velocity.renderer.FrameBuffer;
import velocity.util.MemTracerUtil;
import velocity.util.Point;
import velocity.Rect;
import velocity.config.GlobalAppConfig;

/**
 * Standard sprite representation. Required for use in a Scene.
 */
public abstract class Sprite {
    /**
     * The sprite position and rect.
     */
    public final Rect pos;

    /**
     * The sprite's rotation angle.
     */
    public float rot;

    /**
     * The name of the sprite.
     */
    public final String name;

    /**
     * The z-sorting layer.
     */
    public int sortOrder = 0;

    /**
     * Create a sprite.
     * 
     * @param pos The center position of the sprite.
     * @param rot The sprite's rotation angle.
     * @param name The name of the sprite.
     */
    public Sprite(Point pos, float rot, String name) {
        this.pos = new Rect(pos, 0, 0);
        this.rot = rot;
        this.name = name;

        if (GlobalAppConfig.bcfg.LOG_MEMORY)
            MemTracerUtil.trackSprite(this);
    }

    /**
     * Initialize this sprite. Init is called after scene construction and all 
     * sprites have been created. Alternatively, if the sprite is created in an 
     * existing scene, init is called when the sprite is added to the scene 
     * context (via {@code Scene.currentScene.addSprite(spr)}).
     */
    public void init() {}

    /**
     * Simulate a game tick on this sprite. Called every frame by the active
     * scene.
     */
    public void tick() {}

    /**
     * Delete this sprite and deallocate all used resources. Required in certain
     * cases (where a light source or some other object must be explicitly released).
     * This is called when a scene is destroyed in the scene loading system or if
     * {@code Scene.currentScene.deleteSprite()} is called on this sprite.
     */
    public void delete() {}

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
    @SuppressWarnings("deprecation")
    protected void finalize() throws Throwable {
        super.finalize();
        if (GlobalAppConfig.bcfg.LOG_GC)
            System.out.println("[Scene.GC{Sprite}]: Sprite " + name + " GC'd!");

        if (GlobalAppConfig.bcfg.LOG_MEMORY)
            MemTracerUtil.removeTracking(this);
    }
}
