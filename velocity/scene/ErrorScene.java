package velocity.scene;

import java.awt.Color;

import velocity.Scene;
import velocity.sprite.ui.FPSCounter;
import velocity.util.Point;
import velocity.util.Popup;

/**
 * Scene loaded when a requested scene could not be initialized. Disabled
 * when {@code GlobalAppConfig.SCENE_LOAD_FAILURE_FATAL} is enabled.
 */
public class ErrorScene extends Scene {
    /**
     * Create the scene object.
     * 
     * @param name The name of the scene.
     * @param uuid The scene's unique identifier.
     */
    public ErrorScene(String name, int uuid) {
        super(name, uuid);

        sprites.add(new FPSCounter(new Point(3, 12), 0, "FPS", Color.red));
        // Using default camera in base scene.

        // An error occurred while opening the last scene. Inform the developer of the
        // issue.
        Popup.showWarning("Velocity Scene Warning", 
                          "Failed to load scene! Please check log for details.");
    }
}

