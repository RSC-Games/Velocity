package velocity.sprite.ui.internal;

import velocity.system.Images;
import velocity.Scene;
import velocity.sprite.ui.UIImage;
import velocity.system.SystemResourceLoader;
import velocity.util.Point;
import velocity.util.Timer;

/**
 * Engine internal. Injected by the Scene class into every instantiated scene if an unstable
 * renderer is present.
 */
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
        super(new Point(-250, 115), 0f, name, 
            Images.loadImage(SystemResourceLoader.getSystemResourceLoader(), imgpath));
    }

    /**
     * Only show the pop-up once (even though it's injected into every scene).
     */
    @Override
    public void init() {
        if (hasShown)
            Scene.currentScene.removeSprite(this);

        hasShown = true;
    }

    /**
     * Move the pop-up on-screen, leave it there for a few seconds, then move it back off again.
     */
    @Override
    public void tick() {
        // Show the pop-up.
        if (emerging && this.pos.getPos().x < 200) {
            this.pos.translate(new Point(8, 0));

            if (this.pos.getPos().x > 200) 
                emerging = false;
                toRetract = new Timer(3500, false);
        }

        // If the timer has finished counting down, then retract the popup.
        if (!emerging && !retracting && toRetract.tick())
            retracting = true;

        // Retract the popup.
        if (retracting && this.pos.getPos().x > -250) {
            this.pos.translate(new Point(-8, 0));

            if (this.pos.getPos().x < -250) {
                retracting = false;
                Scene.currentScene.removeSprite(this);
            }
        }
    }
}
