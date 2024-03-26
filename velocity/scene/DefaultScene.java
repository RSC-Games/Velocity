package velocity.scene;

import java.awt.Color;

import velocity.Scene;
import velocity.sprite.ui.FPSCounter;
import velocity.util.Point;

public class DefaultScene extends Scene {
    public DefaultScene(String name, int uuid) {
        super(name, uuid);
        
        /* UI Panel here */
        sprites.add(new FPSCounter(new Point(3, 12), 0, "FPS", Color.yellow));

        /* End UI Panel */

        // Camera required for rendering. DO NOT FORGET!
        // Using default camera in base scene.
    }
}

