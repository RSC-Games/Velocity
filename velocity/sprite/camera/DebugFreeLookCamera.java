package velocity.sprite.camera;

import java.awt.event.KeyEvent;

import velocity.InputSystem;
import velocity.sprite.Camera;
import velocity.util.Point;

public class DebugFreeLookCamera extends Camera {
    /**
     * Create a new camera at a given position following a provided tranform
     * target.
     * 
     * @param pos The camera starting location.
     */
    public DebugFreeLookCamera(Point pos) {
        super(pos);
    }

    /**
     * Camera tick. Allow panning.
     */
    public void tick() {
        super.tick();
        
        Point move = new Point(
            InputSystem.getAxis(KeyEvent.VK_RIGHT, KeyEvent.VK_LEFT) * 10,
            -InputSystem.getAxis(KeyEvent.VK_UP, KeyEvent.VK_DOWN) * 10
        );

        this.transform.translate(move);
    }
}
