package com.rsc_games.velocity.sprite.camera;

import java.awt.event.KeyEvent;

import com.rsc_games.velocity.InputSystem;

import com.rsc_games.velocity.sprite.Camera;
import com.rsc_games.velocity.util.Point;

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
    protected void onTick() {
        super.onTick();
        
        Point move = new Point(
            InputSystem.getAxis(KeyEvent.VK_RIGHT, KeyEvent.VK_LEFT) * 10,
            -InputSystem.getAxis(KeyEvent.VK_UP, KeyEvent.VK_DOWN) * 10
        );

        this.transform.translate(move);
    }
}
