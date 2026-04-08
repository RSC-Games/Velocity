package com.rsc_games.copperheadgl;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Internal. Allow the application to submit drawcalls to the drawqueue.
 * Based on the LVCPU drawqueue implementation.
 */
class GLDrawQueue {
    HashMap<Integer, ArrayList<GLDrawCall>> drawCalls;

    /**
     * Create a new drawqueue for call processing and queuing.
     */
    public GLDrawQueue() {
        this.drawCalls = new HashMap<Integer, ArrayList<GLDrawCall>>();
    }

    /**
     * Submit a draw call for deferred rendering.
     * 
     * @param sortLayer Sorting layer. Lower values are drawn first.
     * @param drawCall Actual call to submit.
     */
    public void pushCall(int sortLayer, GLDrawCall drawCall) {
        ArrayList<GLDrawCall> layer = this.drawCalls.get(sortLayer);

        // Create a new layer for buffering if none exists.
        if (layer == null) {
            //System.out.println("[lvogl]: Adding draw queue layer " + sortLayer);
            layer = new ArrayList<GLDrawCall>();
            this.drawCalls.put(sortLayer, layer);
        }

        //System.out.println("[lvogl]: Layer " + sortLayer + ": Queuing drawcall " + drawCall);
        layer.add(drawCall);
    }

    /**
     * Get the current draw queue for rendering. Generally for an external
     * renderer context to process calls and build a VBO/VAO or something.
     * 
     * @return The queued Draw Calls.
     */
    public HashMap<Integer, ArrayList<GLDrawCall>> getDrawCalls() {
        return this.drawCalls;
    }

    /**
     * Erase all queued drawcalls.
     */
    public void clear() {
        this.drawCalls.clear();
    }

    /**
     * Show this object's internal draw queue.
     * 
     * @return The formatted draw queue.
     */
    public String toString() {
        return this.drawCalls + "";
    }
}
