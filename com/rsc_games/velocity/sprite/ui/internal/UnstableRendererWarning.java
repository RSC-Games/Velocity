package com.rsc_games.velocity.sprite.ui.internal;

import com.rsc_games.velocity.system.Images;

import com.rsc_games.velocity.Rect;
import com.rsc_games.velocity.Scene;
import com.rsc_games.velocity.util.Transform;

import com.rsc_games.velocity.sprite.ui.UIImage;
import com.rsc_games.velocity.system.SystemResourceLoader;
import com.rsc_games.velocity.util.Point;
import com.rsc_games.velocity.util.Timer;

/**
 * Engine internal. Injected by the Scene class into every instantiated scene if an unstable
 * renderer is present.
 * 
 * @deprecated Velocity is removing support for external plug-in renderers, so a unstable warning
 *  is no longer required.
 */
@Deprecated(since="0.7.0.0", forRemoval=true)
public class UnstableRendererWarning extends UIImage {
    /**
     * Only show the warning once.
     */
    static boolean hasShown = false;

    /**
     * State tracking. True if the panel is actively emerging.
     */
    boolean emerging = true;

    /**
     * State tracking. True if the panel is actively retracting.
     */
    boolean retracting = false;

    /**
     * Timer to switch from emerge to retract. 3.5 Seconds.
     */
    Timer toRetract = null;

    /**
     * Create the warning object and position it offscreen.
     * @param name
     * @param imgpath
     */
    public UnstableRendererWarning(String name, String imgpath) {
        super(new Transform(new Rect(new Point(-250, 115), Point.zero), 0f, Point.one, 100), name, 
            Images.loadImage(SystemResourceLoader.getSystemResourceLoader(), imgpath));
    }

    /**
     * Only show the pop-up once (even though it's injected into every scene).
     */
    @Override
    protected void onInit() {
        if (hasShown)
            Scene.currentScene.removeSprite(this);

        hasShown = true;
    }

    /**
     * Move the pop-up on-screen, leave it there for a few seconds, then move it back off again.
     */
    @Override
    protected void onTick() {
        // Show the pop-up.
        if (emerging && this.transform.getPosition().x < 200) {
            this.transform.translate(new Point(8, 0));

            if (this.transform.getPosition().x > 200) 
                emerging = false;
                toRetract = new Timer(3500, false);
        }

        // If the timer has finished counting down, then retract the popup.
        if (!emerging && !retracting && toRetract.tick())
            retracting = true;

        // Retract the popup.
        if (retracting && this.transform.getPosition().x > -250) {
            this.transform.translate(new Point(-8, 0));

            if (this.transform.getPosition().x < -250) {
                retracting = false;
                Scene.currentScene.removeSprite(this);
            }
        }
    }
}
