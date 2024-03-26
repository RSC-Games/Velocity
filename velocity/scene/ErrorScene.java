package velocity.scene;

import java.awt.Color;

import velocity.Scene;
import velocity.sprite.ui.FPSCounter;
import velocity.util.Point;
import velocity.util.Popup;

public class ErrorScene extends Scene {
    public ErrorScene(String name, int uuid) {
        super(name, uuid);
        
        /* UI Panel here */
        sprites.add(new FPSCounter(new Point(3, 12), 0, "FPS", Color.red));
        /* End UI Panel */

        // Camera required for rendering. DO NOT FORGET!
        // Using default camera in base scene.

        // An error occurred while opening the last scene. Show why.
        Popup.showWarning("Velocity Scene Warning", 
                          "Failed to load scene! Please check log for details.");
    }
}

