package velocity.sprite.ui;

import java.awt.Color;

import velocity.InputSystem;
import velocity.Scene;
import velocity.renderer.DrawInfo;
import velocity.renderer.FrameBuffer;
import velocity.util.*;

/**
 * Prints the current screen space mouse coordinates for easier debugging.
 * Part of the modern replacement for the ancient DebugRenderer system.
 */
// TODO: Add autoinjection of this when using the debug renderer config.
// TODO: Don't simulate tick of objects while running the debug renderer.
// TODO: Render collision geometry and stuff.
public class MousePointerTracker extends UIText {
    /**
     * Mouse pointer tracker.
     * 
     * @param pos The screen position.
     * @param rot The rotation angle of the text.
     * @param name The name of the sprite.
     * @param c The text color.
     */
    public MousePointerTracker(Transform transform, String name, Color c) {
        super(transform, name, "Serif", c);
        this.transform.sortOrder = 1;
        this.color = c;
    }

    /**
     * Draw during the UI compositing stage.
     * Counter values updated during this stage.
     * 
     * @param d Draw info.
     * @param fb Rendering framebuffer.
     */
    @Override
    public void renderUI(DrawInfo d, FrameBuffer fb) {    
        String text = "Mouse: " 
            + InputSystem.getMousePos().add(Scene.currentScene.getCamera().transform.location.getDrawLoc());

        Point coords = d.drawRect.getDrawLoc();
        fb.drawText(coords, text, this.font, this.color);
    }
}
