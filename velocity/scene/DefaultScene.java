package velocity.scene;

import java.awt.Color;

import velocity.Scene;
import velocity.sprite.ui.FPSCounter;
import velocity.util.Point;
import velocity.util.Transform;

/**
 * Scene loaded by default when no other scenes could be found.
 * Default scene loads are disabled when {@code GlobalAppConfig.SCENE_LOAD_FAILURE_FATAL}
 * is enabled.
 */
public class DefaultScene extends Scene {
    /**
     * Create the scene object.
     * 
     * @param name The name of the scene.
     * @param uuid The scene's unique identifier.
     */
    public DefaultScene(String name, int uuid) {
        super(name, uuid);
        sprites.add(new FPSCounter(new Transform(new Point(3, 12)), "FPS", Color.yellow));
        // Using default camera in base scene.
    }
}

